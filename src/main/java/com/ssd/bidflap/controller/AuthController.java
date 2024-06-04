package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.dto.FindEmailDto;
import com.ssd.bidflap.domain.dto.LoginDto;
import com.ssd.bidflap.domain.dto.ResetPasswordDto;
import com.ssd.bidflap.domain.dto.SignUpDto;
import com.ssd.bidflap.exception.MemberException;
import com.ssd.bidflap.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("signUpDto", new SignUpDto());
        return "thyme/auth/SignUp";
    }

    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public String signUp(@Valid @ModelAttribute("signUpDto") SignUpDto signUpDto, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {    // valid 바인딩 에러
            return "thyme/auth/SignUp";
        }

        try {
            authService.signUp(signUpDto);
            redirectAttributes.addFlashAttribute("signupSuccess", true);
            return "redirect:/auth/login";
        } catch (MemberException.EmailDuplicatedException e) {  // 서버 에러
            model.addAttribute("emailError", e.getMessage());
        } catch (MemberException.NicknameDuplicatedException e) {
            model.addAttribute("nicknameError", e.getMessage());
        } catch (MemberException.AsDescInputException e) {
            model.addAttribute("asInfoError", e.getMessage());
        } catch (MemberException.AsPriceInputException e) {
            model.addAttribute("asPriceError", e.getMessage());
        }
        return "thyme/auth/SignUp";
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
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "thyme/auth/Login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult bindingResult,
                        HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "thyme/auth/Login";
        }

        try {
            String nickname = authService.login(loginDto);
            session.setAttribute("loggedInMember", nickname);
            redirectAttributes.addFlashAttribute("loginSuccess", true); // 리다이렉트 후 한번만 사용되는 데이터
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());
            return "thyme/auth/Login";
        }
    }

    // 아이디 찾기
    @GetMapping("/find-email")
    public String findEmail(Model model) {
        model.addAttribute("findEmailDto", new FindEmailDto());
        return "thyme/auth/findEmail";
    }

    @PostMapping("/find-email")
    public String findEmail(@Valid @ModelAttribute FindEmailDto findEmailDto, BindingResult bindingResult, Model model) {
        model.addAttribute("findEmailDto", new FindEmailDto());
        return "thyme/auth/findEmail";
    }

    // 비밀번호 재설정
    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        return "thyme/auth/resetPassword";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordDto resetPasswordDto, BindingResult bindingResult, Model model) {
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        return "thyme/auth/resetPassword";
    }
}
