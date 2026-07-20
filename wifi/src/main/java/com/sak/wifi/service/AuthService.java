package com.sak.wifi.service;

import com.sak.wifi.dto.*;
import com.sak.wifi.entity.*;
import com.sak.wifi.exception.ResourceAlreadyExistsException;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @Transactional
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

        String token= UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .company(company)
                .role(Role.EMPLOYEE)
                .status(AccountStatus.UNVERIFIED)
                .verificationToken(token)
                .verificationExpiry(LocalDateTime.now().plusHours(24))
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);

        emailService.sendVerificationEmail(
                user.getEmail(),token
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("user not found"));

        checkAccountStatus(user);

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid credentials");
        }

        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);



        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public LoginResponse refreshToken(RefreshRequest request){

        String refreshToken = request.getRefreshToken();

        RefreshToken refreshToken1= refreshTokenService.findByToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Invalid Refresh Token "));

        refreshTokenService.verifyExpiration(refreshToken1);

        User user= refreshToken1.getUser();

        if (user.getStatus()
                != AccountStatus.ACTIVE) {

            throw new RuntimeException(
                    "Account inactive"
            );
        }

        String accessToken = jwtService.generateAccessToken(user);

        return  LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken1.getToken())
                .build();

    }

    @Transactional
    public  void verifyEmail(String token){

        User user= userRepository.findByVerificationToken(token)
                .orElseThrow(()->new RuntimeException("Invalid Token"));

        if(user.getVerificationExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Link Expired");
        }

        user.setStatus(AccountStatus.ACTIVE);
        user.setVerificationExpiry(null);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    private void handleFailedLogin(User user){

        user.setFailedLoginAttempts(user.getFailedLoginAttempts()+1);
        if(user.getFailedLoginAttempts()>=5){
            user.setStatus(AccountStatus.LOCKED);
            user.setLockTime(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    private void checkAccountStatus(User user){
        if(user.getStatus()==AccountStatus.UNVERIFIED){
            throw new RuntimeException("Verify Email first");
        }
        if(user.getStatus()==AccountStatus.DISABLED){
            throw new RuntimeException("Account Disabled");
        }
        if(user.getStatus()==AccountStatus.LOCKED){
            if(user.getLockTime()
                    .plusMinutes(30)
                    .isBefore(
                            LocalDateTime.now()
                    ))
            {
                user.setStatus(
                        AccountStatus.ACTIVE
                );

                user.setFailedLoginAttempts(0);

                user.setLockTime(null);

                userRepository.save(user);
            }
            throw new RuntimeException("Account Locked. Try Again Later");
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request){
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isEmpty()){
            return;
        }

        User user=optionalUser.get();
        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiry(LocalDateTime.now().plusMinutes(30));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(),token);

    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request){
        Optional<User> optionalUser= userRepository
                .findByResetPasswordToken(request.getToken());

        if(optionalUser.isEmpty()){
            throw new RuntimeException("Invalid token");
        }

        User user=optionalUser.get();


        if(user.getResetPasswordExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token Expired");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);

        userRepository.save(user);
    }

    public void logout(User user){
        refreshTokenService.deleteByUser(user);
    }

}
