package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.dto.LoginDto;
import com.ssd.bidflap.domain.dto.SignUpDto;
import com.ssd.bidflap.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String registerUser(@Valid @ModelAttribute SignUpDto signUpDto) {
        authService.registerMember(signUpDto);
        return "redirect:/auth/login";
    }

    // 로그인
    @GetMapping("/auth/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "thyme/auth/Login";
    }

    @PostMapping("/auth/login")
    public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "thyme/auth/Login";
        }

        try {
            String nickname = authService.login(loginDto);
            session.setAttribute("loggedInMember", nickname);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());
            return "thyme/auth/Login";
        }
    }

}