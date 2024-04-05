package com.bhargav.apigw.multiauthapp.config;

import com.bhargav.apigw.multiauthapp.entity.LoginAttempt;
import com.bhargav.apigw.multiauthapp.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String username = request.getParameter("username");
        String ipAddress = request.getRemoteAddr();
        String password = customPasswordEncoder.encode(request.getParameter("password"));
        LocalDateTime requestTime = LocalDateTime.now();
        LoginAttempt loginAttempt = new LoginAttempt(username, ipAddress, password, requestTime);
        loginAttemptRepository.save(loginAttempt);
        if (exception instanceof OAuth2AuthenticationException) {
            System.out.println("OAuth authentication failed for user: " + username);
        } else {
            System.out.println("Username/password authentication failed for user: " + username);
        }
        response.sendRedirect("/login-error?username=" + username);
    }
}

