package com.sak.wifi.service;

import com.sak.wifi.entity.RefreshToken;
import com.sak.wifi.entity.User;
import com.sak.wifi.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public RefreshToken createRefreshToken(User user){

        String token = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken=
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
