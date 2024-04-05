package com.bhargav.apigw.multiauthapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String welcome() {
        return "welcome";
    }
}
