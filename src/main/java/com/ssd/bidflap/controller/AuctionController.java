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


import java.util.Map;

@Controller

public class AuctionController {
    @Autowired
    private  AuctionService auctionService;
    @Autowired
    private  ProductService productService;


    @GetMapping("/bidder/{id}")
    public String bidderPage(@PathVariable Long id, Model model, HttpSession session ){
        Product product = productService.productView(id);
        model.addAttribute("product", product);
        String nickname = (String) session.getAttribute("loggedInMember");
        model.addAttribute("nickname", nickname);
        return "thyme/bidder/bidder";
    }



    @PostMapping("/start")
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
}

