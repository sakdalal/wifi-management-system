package com.sak.wifi.Controller;

import com.sak.wifi.dto.*;
import com.sak.wifi.entity.User;
import com.sak.wifi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<String>
    registerUser(
            @Valid
            @RequestBody
            RegisterUserRequest request){

        authService.registerUser(request);

        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>
    login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public  ResponseEntity<LoginResponse>
    refreshToken(@RequestBody RefreshRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String>
    logout(
            Authentication authentication){

        User user =
                (User)
                        authentication.getPrincipal();

        authService.logout(user);

        return ResponseEntity.ok(
                "Logged out successfully");
    }

}
