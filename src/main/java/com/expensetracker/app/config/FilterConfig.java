package com.expensetracker.app.config;

import com.expensetracker.app.services.SecurityService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public AuthenticationFilter authenticationFilter(SecurityService securityService) {
        return new AuthenticationFilter(securityService);
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> filterRegistration(AuthenticationFilter filter) {
        FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*"); // Apply to all URLs
        registration.setOrder(1); // High priority
        return registration;
    }
}