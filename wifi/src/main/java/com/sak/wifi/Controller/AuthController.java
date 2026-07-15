package com.sak.wifi.Controller;

import com.sak.wifi.dto.RegisterUserRequest;
import com.sak.wifi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    };
}
