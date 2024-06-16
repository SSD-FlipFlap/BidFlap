package com.ssd.bidflap.controller;


import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.service.MemberService;
import com.ssd.bidflap.service.ProductService;
import com.ssd.bidflap.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private  ProductService productService;
    @Autowired
    private  MemberService memberService;
    private final ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname != null) {
            // 로그인된 경우
            List<Category> interests = memberService.getMemberByInterests(nickname);
            model.addAttribute("interests", interests); // 관심 카테고리..

            List<Product> recommendedProducts;
            if (interests.isEmpty()) {
                recommendedProducts = productService.getPopularProducts();
            } else {
                recommendedProducts = productService.getProductsByCategories(interests);
            }
            model.addAttribute("recommendedProducts", recommendedProducts);
        } else {
            // 로그인되지 않은 경우
            List<Product> popularProducts = productService.getPopularProducts();
            model.addAttribute("popularProducts", popularProducts);
        }
        List<Product> productList = productRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("productList", productList);
      
        return "thyme/main/main";
    }

    @GetMapping("/product")
    public String getProductsByCategory(@RequestParam Category category, Model model) {

        List<Product> productList = new ArrayList<>();
        if (category == Category.ALL) {
            productList = productRepository.findAllByOrderByCreatedAtDesc();
        } else {
            productList = productRepository.findProductsByCategoryOrderByCreatedAtDesc(category);
        }
        model.addAttribute("productList", productList);


        return "thyme/main/main";
    }


}

