package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/my-page")
    public String myPage(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        // 닉네임, 이메일 정보 가져오기
        MemberDto.SimpleInfoResponseDto memberInfo = memberService.getSimpleInfoByNickname(nickname);
        model.addAttribute("memberInfo", memberInfo);
        return "thyme/member/myPage";
    }

    @GetMapping("/delete")
    public String showDeletePage(HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        return "thyme/member/deleteAccount";
    }

}
