package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Purchase;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ProductService;
import com.ssd.bidflap.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    // 상품 구매 페이지 표시
    @PostMapping("/product/purchase")
    public String showPurchasePage(Model model, Long id, HttpSession session) {
        if (id == null) {
            // id가 null인 경우 에러 페이지로 리다이렉트
            return "redirect:/error";
        }

        Product product = productService.productView(id);

        if (product == null) {
            // 상품이 존재하지 않으면 에러 페이지로 리다이렉트
            return "redirect:/error";
        }

        Purchase purchase= Purchase.builder().product(product).build();

        // 모델에 상품 정보 추가
        model.addAttribute("product", product);
        model.addAttribute("purchase", purchase);
        // 구매 페이지로 이동
        return "thyme/product/PurchaseProduct";
    }

    // 상품 구매 확인 처리
    @PostMapping("/product/purchase/confirm")
    public String purchaseProduct(HttpSession session, @ModelAttribute Purchase purchase, @RequestParam("productId") Long productId) {
        String nickname = (String) session.getAttribute("loggedInMember");

        if (nickname == null) {
            return "redirect:/auth/login";
        }
        Product product= productService.productView(productId);
        purchase.setProduct(product);

        purchaseService.registerPurchase(purchase, nickname);

        return "redirect:/";
    }

}

