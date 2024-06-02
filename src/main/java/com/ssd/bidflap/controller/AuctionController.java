package com.ssd.bidflap.controller;


import com.ssd.bidflap.domain.Product;
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
    public String auctionDetail(@RequestParam Long productId, Model model) {
        Product product = productService.productView(productId);

        model.addAttribute("product", product);
        model.addAttribute("auction", product.getAuction());

        return "thyme/bidder/auctionDetail";
    }

    @PostMapping("/bid")
    public String placeBid(HttpSession session, @RequestParam Long productId, @RequestParam int bidPrice, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null || nickname.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            auctionService.placeBid(productId, bidPrice, nickname);
            return "redirect:/auction/detail?productId=" + productId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/auction/detail?productId=" + productId;
        }
    }
}


