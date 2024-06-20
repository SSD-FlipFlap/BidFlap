package com.ssd.bidflap.controller;

import com.ssd.bidflap.interceptor.Auth;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.domain.enums.MemberRole;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ChatService;
import com.ssd.bidflap.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final ChatService chatService;
    private final ProductRepository productRepository;

    @Auth(role = MemberRole.USER)
    @GetMapping("/product/register")
    public String productRegisterForm(HttpSession session, RedirectAttributes redirectAttributes){
        return "thyme/product/RegisterProduct";
    }

    @Auth(role = MemberRole.USER)
    @PostMapping("/product/register")
    public String productRegisterPro (HttpSession session, Product product, @RequestParam("files") List<MultipartFile> files,
                                      RedirectAttributes redirectAttributes) {
        String nickname = (String) session.getAttribute("loggedInMember");
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

        //채팅
        List<ChatRoom> chatRoomList = new ArrayList<>();

        try {
            if (!memberRepository.findByNickname(nickname).isEmpty() && product.getMember().getNickname().equals(nickname)){
                chatRoomList = chatService.findByProductId(id);
            } else if(!memberRepository.findByNickname(nickname).isEmpty())
                chatRoomList = chatService.findByProductIdAndNickname(id, nickname);
            model.addAttribute("sizeOfList", chatRoomList.size());
        } catch(Exception e){
            model.addAttribute("sizeOfList", "채팅방을 찾을 수 없습니다.");
        }
        model.addAttribute("chatRoomList", chatRoomList);

        return "thyme/product/ViewProduct";
    }

    @Auth(role = MemberRole.USER)
    @PostMapping("/product/delete/{id}")
    public String productDelete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String nickname = (String) session.getAttribute("loggedInMember");

        try {
            productService.productDelete(id, nickname);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("deleteDisabled", true);
            return "redirect:/product/view?id=" + id;
        }
        return "redirect:/";
    }

    @Auth(role = MemberRole.USER)
    @GetMapping("/product/modify/{id}")
    public String productModify(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes){
        String nickname = (String) session.getAttribute("loggedInMember");
        Optional<Product> product = productRepository.findById(id);
        if (product.get().getStatus() != ProductStatus.SELLING) {
            redirectAttributes.addFlashAttribute("editDisabled", true);
            return "redirect:/product/view?id=" + id;
        }
        model.addAttribute("product", productService.productView(id));
        return "thyme/product/ModifyProduct";
    }

    @Auth(role = MemberRole.USER)
    @PostMapping("/product/update/{id}")
    public String productUpdate(@PathVariable Long id, Product updatedProduct, HttpSession session,
                                @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                @RequestParam(value = "removedExistingImages", required = false) List<String> removedImageUrls) {
        String nickname = (String) session.getAttribute("loggedInMember");
        productService.updateProduct(id, updatedProduct, files, removedImageUrls, nickname);

        return "redirect:/product/view?id=" + id;
    }

    @Auth(role = MemberRole.USER)
    @PostMapping("/product/like")
    public String likeProduct(@RequestParam("productId") Long productId, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        productService.toggleLike(productId, nickname);

        return "redirect:/product/view?id=" + productId;
    }

    @Auth(role = MemberRole.USER)
    @PostMapping("/product/startAuction")
    public String startAuction(@RequestParam("productId") Long productId, HttpSession session){
        String nickname= (String) session.getAttribute("loggedInMember");
        productService.startAuction(productId, nickname);

        return "redirect:/product/view?id="+ productId;
    }

    @GetMapping("/product/list")
    public String productList(Model model,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "category", required = false) String category) {
        List<Product> products;
        if (category != null && !category.isEmpty()) {
            // 문자열로 된 category를 Category 열거형(Enum)으로 변환
            Category categoryEnum = Category.valueOf(category.toUpperCase());
            products = productService.getProductsByCategory(categoryEnum);
        } else if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchByTitle(keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("type", "search");
        return "thyme/product/ProductViewList";
    }

    @GetMapping("/product/auction")
    public String getOngoingAuction(Model model) {
        List<Product> ongoingAuctions = productRepository.findAllByStatus(ProductStatus.AUCTION);
        model.addAttribute("products", ongoingAuctions);
        model.addAttribute("type", "auction");
        return "thyme/product/ProductViewList";
    }
}