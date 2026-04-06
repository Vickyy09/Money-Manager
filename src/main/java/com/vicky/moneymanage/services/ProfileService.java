package com.vicky.moneymanage.services;


import com.vicky.moneymanage.dto.AuthDTO;
import com.vicky.moneymanage.dto.ProfileDTO;
import com.vicky.moneymanage.entity.Profile;
import com.vicky.moneymanage.repository.ProfileRepository;
import com.vicky.moneymanage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        Profile newProfile=toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        String activationLink=activationURL+"/activate?token=" + newProfile.getActivationToken();
        String subject="Activate your account";
        String body="Click on the following link" + activationLink;
        emailService.sendEmail(newProfile.getEmail(),subject,body);
        return toDTO(newProfile);
    }

    public Profile toEntity(ProfileDTO profileDTO){
        return Profile.builder().id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(Profile profile){
        return ProfileDTO.builder().id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .profileImageUrl(profile.getProfileImageUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public boolean  activate(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;

                })
                .orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(Profile :: getIsActive).orElse(false);
    }

    public Profile getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("Profile not found with email: "+authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        Profile currentUser= null;
        if(email == null){
            currentUser=getCurrentProfile();
        }else{
            currentUser=profileRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email: "+email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .updatedAt(currentUser.getUpdatedAt())
                .createdAt(currentUser.getCreatedAt())
                .build();
    }

    public Map<String, Object> authenciateAndGenerate(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail()  ,authDTO.getPassword()));
            String token=jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDTO.getEmail())
            );
        }catch (Exception e) {
throw new RuntimeException("Invalid email or password");
}

    }
}
