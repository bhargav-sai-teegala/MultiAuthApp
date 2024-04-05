package com.bhargav.apigw.multiauthapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError() {
        return "login-error";
    }

    @GetMapping("/welcome")
    public String welcome(Principal principal, Model model) {
        String username = getUsername(principal);

        if ("admin".equals(username)) {
            model.addAttribute("message", "Hey welcome Admin.");
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticationType = getAuthenticationType(authentication);

            if ("facebook".equals(authenticationType)) {
                model.addAttribute("message", "Welcome " + username + ", you have chosen Facebook Authentication.");
            } else if ("google".equals(authenticationType)) {
                model.addAttribute("message", "Welcome " + username + ", you have chosen Google Authentication.");
            } else {
                // Default message for other authentication methods
                model.addAttribute("message", "Welcome " + username + ".");
            }
        }

        return "welcome";
    }

    private String getAuthenticationType(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        if (attributes.containsKey("email") && attributes.containsKey("name") && !attributes.containsKey("sub")) {
            return "facebook";
        } else if (attributes.containsKey("email") && attributes.containsKey("sub")) {
            return "google";
        } else {
            return "unknown";
        }
    }

    private String getUsername(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) principal).getPrincipal();
            return oauth2User.getAttribute("name");
        } else {
            return principal.getName();
        }
    }
}
