package com.vicky.moneymanage.controller;

import com.vicky.moneymanage.dto.AuthDTO;
import com.vicky.moneymanage.dto.ProfileDTO;
import com.vicky.moneymanage.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registerProfile= profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivate=profileService.activate(token);
        if(isActivate){
            return ResponseEntity.ok("Profile is activated");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile is not activated");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "Message","Account is not active.please activate your account"
                ));
            }
            Map<String,Object>response=profileService.authenciateAndGenerate(authDTO);
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Message", e.getMessage()
            ));
        }
    }
    @GetMapping("/test")
    public String test(){
        return "Test Successful";
    }
}
