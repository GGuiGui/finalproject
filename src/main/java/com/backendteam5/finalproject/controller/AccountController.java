package com.backendteam5.finalproject.controller;

import com.backendteam5.finalproject.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/account/login")
    public String login() {
        return "login";
    }

    @GetMapping("/account/signup")
    public String signup() {
        return "signup";
    }
}
