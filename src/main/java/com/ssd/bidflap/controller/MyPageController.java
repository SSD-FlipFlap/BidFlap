package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.ProductLike;
import com.ssd.bidflap.domain.Purchase;
import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final ChatService chatService;
    private final ProductService productService;
    private final PurchaseService purchaseService;
    private final AuctionService auctionService;

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
    public String myProduct(@RequestParam(required = false) String status, HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        List<Product> productList = new ArrayList<>();
        if (status != null) {
            productList = productService.getProductByMemberAndStatus(nickname, status);
        }
        else {
            productList = productService.getProductByMember(nickname);    // 전체
        }

        model.addAttribute("productList", productList);

        return "thyme/member/myProduct";
    }

    // 구매 내역
    @GetMapping("/my-page/purchase")
    public String myPurchase(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        List<Purchase> purchaseList = purchaseService.getPurchaseByMember(nickname);
        model.addAttribute("purchaseList", purchaseList);

        return "thyme/member/myPurchase";
    }

    // 경매 내역
    @GetMapping("/my-page/auction")
    public String myAuction(@RequestParam(required = false) ProductStatus status, HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        List<Product> auctionProductList = new ArrayList<>();

        if (status == null) {   // 전체
            auctionProductList = auctionService.getAllProductsByMemberId(nickname);
        } else if (status.equals(ProductStatus.AUCTION_WON)) {  // 낙찰
            auctionProductList = auctionService.getAuctionWonProductsByMemberIdAndStatus(nickname);
        } else {
            auctionProductList = auctionService.getProductsByMemberIdAndStatus(nickname, status);
        }

        model.addAttribute("productList", auctionProductList);

        return "thyme/member/myAuction";
    }

    // 좋아요 내역
    @GetMapping("/my-page/like")
    public String myLike(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        List<ProductLike> productLikeList = productService.getProductLikeByMember(nickname);
        model.addAttribute("likeList", productLikeList);

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
