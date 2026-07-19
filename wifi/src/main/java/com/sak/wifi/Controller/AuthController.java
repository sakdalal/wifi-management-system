package com.sak.wifi.Controller;

import com.sak.wifi.dto.*;
import com.sak.wifi.entity.User;
import com.sak.wifi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/verify-email")
    public ResponseEntity<String>
    verifyEmail(@RequestParam String token){
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified Successfully");
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String>
    forgotPassword(@RequestBody ForgotPasswordRequest request){
        authService.forgotPassword(request);
        return ResponseEntity.ok("If account exists,reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String>
    resetPassword(@RequestBody ResetPasswordRequest request){
        authService.resetPassword(request);
        return ResponseEntity.ok("Password updated Successfully");
    }
}
