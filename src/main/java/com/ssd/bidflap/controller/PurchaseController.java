package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Purchase;
import com.ssd.bidflap.domain.enums.MemberRole;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.interceptor.Auth;
import com.ssd.bidflap.repository.AuctionRepository;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.AuctionService;
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
    private final AuctionService auctionService;
    private final PurchaseService purchaseService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;

    // 상품 구매 페이지 표시
    @Auth(role = MemberRole.USER)
    @PostMapping("/product/purchase")
    public String showPurchasePage(Model model, Long id) {
        if (id == null) {
            return "redirect:/error";
        }
        Product product = productService.productView(id);
        if (product == null) {
            return "redirect:/error";
        }
        // 경매 정보 가져오기
        Auction auction = auctionRepository.findByProductId(id);

        if (auction != null) {
            if (auction.getSuccessfulBidder() != null) {
                product.setFinalBidPrice(auction.getHighPrice());
            }
            // 상품 상태를 AUCTION_ENDED로 변경
            product.setStatus(ProductStatus.AUCTION_ENDED);

            // 보증금 반환
            auction.getBidderList().stream()
                    .filter(bidder -> !bidder.getMember().getId().equals(auction.getSuccessfulBidder()))
                    .forEach(bidder -> {
                        Member memberToRefund = bidder.getMember();
                        memberToRefund.setDepositBalance(memberToRefund.getDepositBalance() + bidder.getDeposit());
                        memberRepository.save(memberToRefund);
                    });

            // 낙찰자의 보증금 반환
            auction.getBidderList().stream()
                    .filter(bidder -> bidder.getMember().getId().equals(auction.getSuccessfulBidder()))
                    .findFirst()
                    .ifPresent(bidder -> {
                        Member winner = bidder.getMember();
                        winner.setDepositBalance(winner.getDepositBalance() + bidder.getDeposit());
                        memberRepository.save(winner);
                    });

            auctionRepository.save(auction);
        }

        productRepository.save(product);

        Purchase purchase = Purchase.builder().product(product).build();

        model.addAttribute("product", product);
        model.addAttribute("loggedUser", nickname);
        model.addAttribute("purchase", purchase);
        return "thyme/product/PurchaseProduct";
    }

    // 상품 구매 확인 처리
    @Auth(role = MemberRole.USER)
    @PostMapping("/product/purchase/confirm")
    public String purchaseProduct(@ModelAttribute Purchase purchase, @RequestParam Long productId, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        Product product = productService.productView(productId);
        purchase.setProduct(product);

        purchaseService.registerPurchase(purchase, nickname);

        return "redirect:/";
    }
}

