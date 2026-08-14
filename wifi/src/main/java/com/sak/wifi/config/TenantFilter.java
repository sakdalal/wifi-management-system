package com.sak.wifi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String companyIdHeader= request.getHeader("X-Company-Id");

        try{

            if(companyIdHeader==null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("X-Company-Id header is required");
                return;
            }
            Long companyId=Long.parseLong(companyIdHeader);
            TenantContext.setCompanyId(companyId);
            filterChain.doFilter(request,response);

        }finally {
            TenantContext.clear();
        }

    }
}
