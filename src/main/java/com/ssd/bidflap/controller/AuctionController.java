package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.dto.BidderDto;
import com.ssd.bidflap.repository.AuctionRepository;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.service.AuctionService;
import com.ssd.bidflap.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

@Controller
public class AuctionController {

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/bidder/{id}")
    public String bidderPage(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.productView(id);
        model.addAttribute("product", product);
        String nickname = (String) session.getAttribute("loggedInMember");
        model.addAttribute("nickname", nickname);
        return "thyme/bidder/bidder";
    }

    // 경매 시작
    @PostMapping("/auction/start")
    public ResponseEntity<String> startAuction(@RequestParam Long id, @RequestParam int duePeriod) {
        try {
            auctionService.startAuction(id, duePeriod);
            return ResponseEntity.ok("경매가 시작되었습니다!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 경매 상세페이지
    @GetMapping("/auction/detail")
    public String auctionDetail(@RequestParam Long productId, Model model, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        if (productId == null) {
            return "redirect:/error"; // productId가 null인 경우 에러 페이지로 리다이렉트
        }

        Product product = productService.productView(productId);
        Auction auction = product.getAuction();
        if (auction == null) {
            return "redirect:/error"; // 경매가 없는 경우 에러 페이지로 리다이렉트
        }

        Long successfulBidderId = auction.getSuccessfulBidder();
        String successfulBidderNickname = "낙찰자 없음";

        if (successfulBidderId != null) {
            Optional<Member> successfulBidderOptional = memberRepository.findById(successfulBidderId);
            if (successfulBidderOptional.isPresent()) {
                Member successfulBidder = successfulBidderOptional.get();
                successfulBidderNickname = successfulBidder.getNickname();
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("auction", auction);
        model.addAttribute("loggedUser", nickname);
        model.addAttribute("successfulBidderNickname", successfulBidderNickname);

        return "thyme/bidder/auctionDetail";
    }

    // 입찰
    @PostMapping("/send/bid/{productId}")
    public ResponseEntity<?> placeBid(@PathVariable Long productId, @RequestBody BidderDto bidRequest) {
        try {
            auctionService.placeBid(productId, bidRequest.getPrice(), bidRequest.getNickname());
            return ResponseEntity.ok("Bid placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place bid: " + e.getMessage());
        }
    }

    // 구매 취소
    @PostMapping("/auction/cancel/{productId}")
    public String cancelPurchase(@PathVariable Long productId, @RequestParam String nickname, RedirectAttributes redirectAttributes) {
        try {
            auctionService.cancelAuctionPurchase(productId, nickname);
            redirectAttributes.addFlashAttribute("message", "낙찰자의 구매가 취소되었습니다!");
            return "redirect:/auction/detail?productId=" + productId; // 경매 상세 페이지 경로로 리다이렉트
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auction/detail?productId=" + productId; // 에러 발생 시 경매 상세 페이지 경로로 리다이렉트
        }
    }

}
