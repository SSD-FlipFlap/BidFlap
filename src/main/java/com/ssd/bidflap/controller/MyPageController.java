package com.ssd.bidflap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/members")
public class MyPageController {

    @GetMapping("/my-page")
    public String myPage() {
        return "thyme/member/myPage";
    }

}
