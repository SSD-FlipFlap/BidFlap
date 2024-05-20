package com.ssd.bidflap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
public class MyPageController {

    @GetMapping("/my-page")
    public String myPage() {
        return "thyme/member/myPage";
    }

}
