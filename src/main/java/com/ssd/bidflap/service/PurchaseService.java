package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Purchase;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Purchase registerPurchase(Purchase purchase, String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        purchase.setMember(member);

        return purchaseRepository.save(purchase);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchaseByMember(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return purchaseRepository.findByMember(member);
    }
}
