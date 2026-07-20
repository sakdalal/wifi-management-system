package com.sak.wifi.service;

import com.sak.wifi.entity.RefreshToken;
import com.sak.wifi.entity.User;
import com.sak.wifi.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;


    @Transactional
    public RefreshToken createRefreshToken(User user){

        System.out.println("Before delete:");
        System.out.println(tokenRepository.findAll());
        deleteByUser(user);
        System.out.println("After delete:");
        System.out.println(tokenRepository.findAll());

        String token = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(token)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .user(user)
                        .build();

        return tokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token){

        if(token.getExpiryDate().isBefore(LocalDateTime.now())){
            tokenRepository.delete(token);

            throw new RuntimeException("Refresh Token Expired");
        }

        return token;

    }

    public Optional<RefreshToken>
    findByToken(String token){
        return tokenRepository.findByToken(token);
    }

    public void deleteByUser(User user){
        tokenRepository.deleteByUser(user);
    }


}
