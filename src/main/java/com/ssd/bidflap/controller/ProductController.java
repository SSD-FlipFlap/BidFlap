package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @GetMapping("/product/view")
    public String productView(Model model, Long id){
        model.addAttribute("product", productService.productView(id));
        return "thyme/product/ViewProduct";
    }

    @GetMapping("/product/delete")
    public String productDelete(Long id){

        productService.productDelete(id);

        return "redirect:/";
    }

    @GetMapping("/product/list")
    public String productList(Model model, @RequestParam(value="keyword", required = false) String keyword) {
        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchByTitle(keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "thyme/product/ProductViewList";
    }

}
