package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.dto.LoginDto;
import com.ssd.bidflap.domain.dto.SignUpDto;
import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.service.AuthService;
import com.ssd.bidflap.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final MemberService memberService;

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

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutSuccess", "true");
        return "redirect:/";
    }

    // 프로필 관리
    @GetMapping("/members/edit-profile")
    public String editProfile(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        // 기존 회원 정보 가져오기
        MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);

        model.addAttribute("changePasswordDto", new MemberDto.ChangePasswordDto());
        model.addAttribute("updateMemberDto", memberInfo);
        return "thyme/member/editProfile";
    }

    // 비밀번호 변경
    @PostMapping("/members/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") MemberDto.ChangePasswordDto changePasswordDto,
                                 BindingResult result, HttpSession session, // bindingResilt는 @ModelAttribute 다음에 위치해야 한다.
                                 Model model, RedirectAttributes redirectAttributes) {
        // 로그인한 회원의 닉네임(세션에 저장된 값)
        String nickname = (String) session.getAttribute("loggedInMember");

        if (nickname == null) {
            return "redirect:/auth/login";  // 로그인 요청
        }

        if (result.hasErrors()) {
            MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);
            model.addAttribute("updateMemberDto", memberInfo);
            model.addAttribute("changePasswordDto", changePasswordDto);
            return "thyme/member/editProfile";
        }

        try {
            memberService.changePassword(nickname, changePasswordDto);
            redirectAttributes.addFlashAttribute("updateSuccess", true);
            return "redirect:/members/edit-profile";
        } catch (IllegalArgumentException e) {
            MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);
            model.addAttribute("updateMemberDto", memberInfo);
            model.addAttribute("passwordError", e.getMessage());
            return "thyme/member/editProfile";
        } catch (IllegalStateException e) {
            MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);
            model.addAttribute("updateMemberDto", memberInfo);
            model.addAttribute("newPasswordError", e.getMessage());
            return "thyme/member/editProfile";
        }
    }

    // 회원 정보 수정
    @PostMapping("/members/update-member")
    public String updateMember(@Valid @ModelAttribute("updateMemberDto") MemberDto.UpdateMemberDto updateMemberDto,
                                 BindingResult result, HttpSession session,
                                 Model model, RedirectAttributes redirectAttributes) {
        // 로그인한 회원의 닉네임(세션에 저장된 값)
        String nickname = (String) session.getAttribute("loggedInMember");

        if (nickname == null) {
            return "redirect:/auth/login";  // 로그인 요청
        }

        if (result.hasErrors()) {
            model.addAttribute("changePasswordDto", new MemberDto.ChangePasswordDto());
            model.addAttribute("updateMemberDto", updateMemberDto);
            return "thyme/member/editProfile";
        }
        try {
            // 세션의 닉네임 갱신
            String newNickname = memberService.updateMember(nickname, updateMemberDto);
            session.setAttribute("loggedInMember", newNickname);

            redirectAttributes.addFlashAttribute("updateMemberSuccess", true);
            return "redirect:/members/edit-profile";
        } catch (IllegalStateException e) {
            model.addAttribute("duplicatedError", e.getMessage());
            model.addAttribute("changePasswordDto", new MemberDto.ChangePasswordDto());
            return "thyme/member/editProfile";
        }
    }

    // 프로필 사진 변경

}