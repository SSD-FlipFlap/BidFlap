package com.ssd.bidflap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
@Controller
public class BidderController {
    @GetMapping("/bidder")
    public String BidderPage() {
        return "thyme/bidder/bidder";
    }
}
