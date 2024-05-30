package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/register")
    public String productRegisterForm(HttpSession session, Model model){

        return "thyme/product/RegisterProduct";
    }

    @PostMapping("/product/register")
    public String productRegisterPro (HttpSession session, Product product, @RequestParam("files") List<MultipartFile> files,
                                      RedirectAttributes redirectAttributes) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login"; // 로그인되지 않은 경우 로그인 페이지로
        }

        Product newProduct = productService.registerProduct(product, files, nickname);

        // 상품 등록하고 상품의 ID를 가져와서 상세 페이지로
        Long productId = newProduct.getId();
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

        boolean isAuthor = product.getMember().getNickname().equals(nickname);
        model.addAttribute("isAuthor", isAuthor);

        Integer likeCount = product.getLikeCount();
        model.addAttribute("likeCount", likeCount);

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
    public String productUpdate(@PathVariable Long id, Product updatedProduct, HttpSession session,
                                @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                @RequestParam(value = "removedExistingImages", required = false) List<String> removedImageUrls) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }
        productService.updateProduct(id, updatedProduct, files, removedImageUrls, nickname);

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