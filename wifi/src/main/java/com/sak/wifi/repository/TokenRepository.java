package com.sak.wifi.repository;

import com.sak.wifi.entity.RefreshToken;
import com.sak.wifi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken,Long> {

   Optional<RefreshToken>
    findByToken(String token);

   void deleteByUser(User user);
}
