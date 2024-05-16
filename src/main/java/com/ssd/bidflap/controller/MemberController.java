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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;

    // 회원가입
    @GetMapping("/auth/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("signUpDto", new SignUpDto());
        return "thyme/auth/SignUp";
    }

    @PostMapping("/auth/signup")
    public String signUp(@Valid @ModelAttribute("signUpDto") SignUpDto signUpDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {    // valid 바인딩 에러
            return "thyme/auth/SignUp";
        }

        try {
            authService.signUp(signUpDto);
            return "redirect:/auth/login";
        } catch (IllegalStateException e) {  // 서버 에러
            if (e.getMessage().contains("이메일")) {
                model.addAttribute("emailError", e.getMessage());
            } else if (e.getMessage().contains("닉네임")) {
                model.addAttribute("nicknameError", e.getMessage());
            }
            return "thyme/auth/SignUp";
        }
    }

    // 뷰에 은행 목록을 전달(drop-down list)
    @ModelAttribute("bankList")
    public List<String> bankList() {
        List<String> bankList = new ArrayList<>();
        bankList.add("국민");
        bankList.add("신한");
        bankList.add("우리");
        bankList.add("하나");
        bankList.add("농협");
        bankList.add("수협");
        bankList.add("산업");
        bankList.add("카카오뱅크");
        return bankList;
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