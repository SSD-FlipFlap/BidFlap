package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/product/register")
    public String productRegisterForm(){
        return "thyme/product/RegisterProduct";
    }

    @PostMapping("/product/registerdo")
    public String productRegisterPro (Product product/*, MultipartFile product_filepath*/) throws Exception{
//        LocalDateTime currentTime = LocalDateTime.now();

//        product.setCreatedAt(currentTime);

        productService.registerProduct(product/*, product_filepath*/);
        return "";
    }
}
