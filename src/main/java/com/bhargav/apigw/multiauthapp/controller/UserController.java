package com.bhargav.apigw.multiauthapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/userLogin")
    public String getMessage() {
        return "Hey Welcome Admin";
    }
}
