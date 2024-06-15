package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.ProductLike;
import com.ssd.bidflap.domain.Purchase;
import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.repository.PurchaseRepository;
import com.ssd.bidflap.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final ChatService chatService;
    private final ProductService productService;
    private final PurchaseService purchaseService;
    private final AuctionService auctionService;
    private final PurchaseRepository purchaseRepository;

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

        // 판매 내역 개수 가져오기
        int sellingProducts = productService.countProductsByMemberAndStatus(nickname, ProductStatus.SELLING);
        int auctionProducts = productService.countProductsByMemberAndStatus(nickname, ProductStatus.AUCTION);
        int soldProducts = productService.countProductsByMemberAndStatus(nickname, ProductStatus.SOLD);
        int totalProducts = sellingProducts + auctionProducts + soldProducts;

        model.addAttribute("sellingProducts", sellingProducts);
        model.addAttribute("auctionProducts", auctionProducts);
        model.addAttribute("soldProducts", soldProducts);
        model.addAttribute("totalProducts", totalProducts);

        // 경매 내역 개수 가져오기
        int ongoingAuctions = auctionService.countAuctionProductsByMemberIdAndStatus(nickname, ProductStatus.AUCTION);
        int endedAuctions = auctionService.countAuctionProductsByMemberIdAndStatus(nickname, ProductStatus.AUCTION_ENDED);
        int successfulAuctions = auctionService.countSuccessfulBidProductsByMember(nickname);
        int totalAuctions = ongoingAuctions + endedAuctions;

        model.addAttribute("ongoingAuctions", ongoingAuctions);
        model.addAttribute("endedAuctions", endedAuctions);
        model.addAttribute("successfulAuctions", successfulAuctions);
        model.addAttribute("totalAuctions", totalAuctions);

        // 구매 내역 4개
        List<Purchase> purchaseList = purchaseService.getPurchaseByMember(nickname);
        List<Purchase> recentPurchaseList = purchaseList.stream().limit(4).collect(Collectors.toList());
        model.addAttribute("purchaseList", recentPurchaseList);

        // 좋아요 내역 4개
        List<ProductLike> productLikeList = productService.getProductLikeByMember(nickname);
        List<ProductLike> recentProductLikeList = productLikeList.stream().limit(4).collect(Collectors.toList());
        model.addAttribute("likeList", recentProductLikeList);

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

    // 구매 내역 상세
    @GetMapping("/my-page/purchase/{id}")
    public String myPurchase(@PathVariable Long id, HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("구매 정보를 찾을 수 없습니다."));
        model.addAttribute("purchase", purchase);

        return "thyme/member/myPurchaseDetail";
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
