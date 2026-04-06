package com.vicky.moneymanage.services;

import com.vicky.moneymanage.entity.Profile;
import com.vicky.moneymanage.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Profile existingProfile= profileRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Profile Not found with email"+email));
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList()).build();

    }
}
