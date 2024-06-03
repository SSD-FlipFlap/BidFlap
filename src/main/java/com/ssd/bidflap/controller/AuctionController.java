package com.ssd.bidflap.controller;


import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.dto.BidderDto;
import com.ssd.bidflap.service.AuctionService;
import com.ssd.bidflap.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller

public class AuctionController {
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping("/bidder/{id}")
    public String bidderPage(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.productView(id);
        model.addAttribute("product", product);
        String nickname = (String) session.getAttribute("loggedInMember");
        model.addAttribute("nickname", nickname);
        return "thyme/bidder/bidder";
    }


    @PostMapping("/auction/start")
    public ResponseEntity<String> startAuction(@RequestParam Long id, @RequestParam int duePeriod) {
        try {
            auctionService.startAuction(id, duePeriod);
            return ResponseEntity.ok("경매가 시작되었습니다!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/auction/detail")
    public String auctionDetail(@RequestParam Long productId, Model model, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        Product product = productService.productView(productId);
        model.addAttribute("product", product);
        model.addAttribute("auction", product.getAuction());
        model.addAttribute("loggedUser", nickname);
        return "thyme/bidder/auctionDetail";
    }

    @PostMapping("/send/bid/{productId}")
    public ResponseEntity<?> placeBid(@PathVariable Long productId, @RequestBody BidderDto bidRequest) {
        try {
            auctionService.placeBid(productId, bidRequest.getPrice(), bidRequest.getNickname());
            return ResponseEntity.ok("Bid placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place bid: " + e.getMessage());
        }
    }

}


