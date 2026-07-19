package com.sak.wifi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String token){
        String link= "http://localhost:8080/auth/verify-email?token="+token;
        SimpleMailMessage mail=new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Verify you email");
        mail.setText("Click below link:\n"+link);

        mailSender.send(mail);
    }

    public void sendPasswordResetEmail(String email,String token){
        String link= "http://localhost:8080/auth/reset-password?token="+token;
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset password");
        message.setText("Click below link to reset password:\n"+link);

        mailSender.send(message);
    }
}
