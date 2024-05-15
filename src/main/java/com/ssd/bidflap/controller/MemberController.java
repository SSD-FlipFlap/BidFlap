package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.dto.SignUpDto;
import com.ssd.bidflap.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;

    // 회원가입
    @GetMapping("/auth/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("signupForm", new SignUpDto());
        return "thyme/auth/SignUp";
    }

    @PostMapping("/auth/signup")
    public String registerUser(@ModelAttribute @Valid SignUpDto signUpDto) {
        authService.registerMember(signUpDto);
        return "redirect:/auth/login";
    }

    // 로그인
    @GetMapping("/auth/login")
    public String showLoginForm(Model model) {
        return "thyme/auth/Login";
    }
}