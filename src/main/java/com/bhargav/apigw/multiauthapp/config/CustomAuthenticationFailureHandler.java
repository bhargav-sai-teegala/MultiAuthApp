package com.bhargav.apigw.multiauthapp.config;

import com.bhargav.apigw.multiauthapp.entity.LoginAttempt;
import com.bhargav.apigw.multiauthapp.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String ipAddress = request.getRemoteAddr();

        LoginAttempt loginAttempt = new LoginAttempt(username, ipAddress);
        loginAttemptRepository.save(loginAttempt);
        System.out.println("Authentication failed: " + exception.getMessage());

        response.sendRedirect("/login-error");
    }
}

