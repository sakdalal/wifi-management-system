package com.sak.wifi.security;

import com.sak.wifi.service.CustomUserDetailsService;
import com.sak.wifi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        if (authHeader==null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
        }

        String token =authHeader.substring(7);

        String email= jwtService.extractUsername(token);

        UserDetails userDetails= userDetailsService.loadUserByUsername(email);

        if(jwtService.isTokenValid(token,userDetails)){
            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails
                                    .getAuthorities()
                    );
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);

    }




}
