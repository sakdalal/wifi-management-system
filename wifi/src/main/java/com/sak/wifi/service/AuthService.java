package com.sak.wifi.service;

import com.sak.wifi.dto.LoginRequest;
import com.sak.wifi.dto.LoginResponse;
import com.sak.wifi.dto.RegisterUserRequest;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Role;
import com.sak.wifi.entity.User;
import com.sak.wifi.exception.ResourceAlreadyExistsException;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void registerUser(RegisterUserRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException("Email Already Exists");
        }

        Company company =
                companyRepository.findById(
                        request.getCompanyId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Company not found"
                        )
                );

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .company(company)
                .role(Role.EMPLOYEE)
                .isEnabled(true)
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user= userRepository.findByEmail(request.getEmail()).orElseThrow();

        String token= jwtService.generateToken(user);

        return new LoginResponse(token);
    }

}
