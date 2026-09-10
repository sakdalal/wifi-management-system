package com.sak.wifi.service;

import com.sak.wifi.dto.UpdateProfileRequest;
import com.sak.wifi.dto.UserProfileResponse;
import com.sak.wifi.entity.User;
import com.sak.wifi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(String email){
        User user= userRepository.findByEmail(email)
                .orElseThrow(()->
                    new RuntimeException("User not found")
                );

        return  UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request){

        User user= userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        if(!user.getEmail().equals(request.getEmail())
            && userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        return  UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .build();
    }
}
