package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model) {
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

