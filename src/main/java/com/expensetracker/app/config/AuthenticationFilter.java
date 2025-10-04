package com.expensetracker.app.config;

import com.expensetracker.app.services.SecurityService;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private final SecurityService securityService;

    public AuthenticationFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userId = httpRequest.getHeader("X-User-Id");
        
        if (userId != null && !userId.isBlank()) {
            securityService.setCurrentUser(userId);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            securityService.clearCurrentUser();
        }
    }
}