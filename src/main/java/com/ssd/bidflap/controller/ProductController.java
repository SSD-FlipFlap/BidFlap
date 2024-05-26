package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.MemberService;
import com.ssd.bidflap.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/product/register")
    public String productRegisterForm(HttpSession session, Model model){

        return "thyme/product/RegisterProduct";
    }

    @PostMapping("/product/register")
    public String productRegisterPro (HttpSession session, Product product, RedirectAttributes redirectAttributes) throws Exception{
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login"; // 로그인되지 않은 경우 로그인 페이지로
        }

        productService.registerProduct(product, nickname);

        if (product.getLikeCount() == null) {
            product.setLikeCount(0);
        }
        // 상품 등록하고 상품의 ID를 가져와서 상세 페이지로
        Long productId = productService.getProductId(product);
        redirectAttributes.addAttribute("id", productId);
        return "redirect:/product/view";
    }

    @GetMapping("/product/view")
    public String productView(Model model, Long id, HttpSession session) {
        Product product = productService.productView(id);
        model.addAttribute("product",  product);

        String nickname = (String) session.getAttribute("loggedInMember");
        model.addAttribute("nickname", nickname);

        Boolean isProductLiked = (nickname != null) && productService.isProductLikedByMember(id, nickname);
        model.addAttribute("isProductLiked", isProductLiked);

        return "thyme/product/ViewProduct";
    }


    @PostMapping("/product/delete/{id}")
    public String productDelete(@PathVariable Long id, HttpSession session){

        productService.productDelete(id);

        return "redirect:/";
    }

    @GetMapping("/product/modify/{id}")
    public String productModify(@PathVariable Long id, Model model){

        model.addAttribute("product", productService.productView(id));
        return "thyme/product/ModifyProduct";
    }

    @PostMapping("product/update/{id}")
    public String productUpdate(@PathVariable Long id, Product product, HttpSession session, Model model, RedirectAttributes redirectAttributes) throws Exception{
        String nickname = (String) session.getAttribute("loggedInMember");
        model.addAttribute("nickname", nickname);

        Product productTemp = productService.productView(id);
        productTemp.setTitle(product.getTitle());
        productTemp.setDescription(product.getDescription());
        productTemp.setPrice(product.getPrice());
        productTemp.setCategory(product.getCategory());

        productService.registerProduct(productTemp, nickname);

        return "redirect:/product/view?id=" + id;
    }

    @PostMapping("/product/like")
    public String likeProduct(@RequestParam("productId") Long productId, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login"; // 로그인되지 않은 경우 로그인 페이지로
        }

        productService.toggleLike(productId, nickname);

        return "redirect:/product/view?id=" + productId;
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