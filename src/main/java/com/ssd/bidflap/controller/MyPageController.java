package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.service.ChatService;
import com.ssd.bidflap.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final ChatService chatService;

    // 마이페이지 홈
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

    // 회원 탈퇴
    @GetMapping("/delete")
    public String showDeletePage(HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        return "thyme/member/deleteAccount";
    }

    // 판매 내역
    @GetMapping("/my-page/product")
    public String myProduct(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        return "thyme/member/myProduct";
    }

    // 구매 내역
    @GetMapping("/my-page/purchase")
    public String myPurchase(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        return "thyme/member/myPurchase";
    }

    // 경매 내역
    @GetMapping("/my-page/auction")
    public String myAuction(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        return "thyme/member/myAuction";
    }

    // 좋아요 내역
    @GetMapping("/my-page/like")
    public String myLike(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        return "thyme/member/myLike";
    }

    // 판매글 채팅 내역
    @GetMapping("/my-page/chat/product")
    public String myProductChat(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        List<ChatRoom> chatRooms = chatService.getProductChatRoomListByNickname(nickname);
        model.addAttribute("productChatRooms", chatRooms);

        return "thyme/member/myChat";
    }

    // as 채팅 내역
    @GetMapping("/my-page/chat/as")
    public String myAsChat(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        List<ChatRoom> chatRooms = chatService.getAsChatRoomListByNickname(nickname);
        model.addAttribute("asChatRooms", chatRooms);

        return "thyme/member/myChat";
    }

}
