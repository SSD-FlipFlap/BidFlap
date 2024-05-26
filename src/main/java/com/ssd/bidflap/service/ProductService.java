package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.ProductLike; // 추가: ProductLike 엔티티 import
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.repository.ProductLikeRepository; // 추가: ProductLikeRepository import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository; // 추가: ProductLikeRepository 의존성 주입
    @Autowired
    private HttpSession httpSession;

    public void registerProduct(Product product, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member persistedMember = optionalMember.get();
        product.setMember(persistedMember);
        productRepository.save(product);
    }

    public Long getProductId(Product product) {
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    public Product productView(Long id) {
        return productRepository.findById(id).orElse(null); // 변경: Optional.get() 대신 Optional.orElse(null) 사용
    }

    public void productDelete(Long id) {
        productRepository.deleteById(id);
    }


    public void toggleLike(Long productId, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("해당하는 상품을 찾을 수 없습니다.");
        }
        Product product = optionalProduct.get();

        Optional<ProductLike> optionalProductLike = productLikeRepository.findByMemberAndProduct(member, product);

        if (optionalProductLike.isPresent()) {
            productLikeRepository.delete(optionalProductLike.get());
            product.setLikeCount(product.getLikeCount() - 1); // 좋아요 개수 감소
        } else {
            ProductLike productLike = ProductLike.builder()
                    .member(member)
                    .product(product)
                    .build();
            productLikeRepository.save(productLike);
            product.setLikeCount(product.getLikeCount() != null ? product.getLikeCount() + 1 : 1); // 좋아요 개수 증가
        }
        productRepository.save(product);
    }

    public boolean isProductLikedByMember(Long productId, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            return false;
        }
        Member member = optionalMember.get();

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return false;
        }
        Product product = optionalProduct.get();

        return productLikeRepository.findByMemberAndProduct(member, product).isPresent();
    }

    public List<Product> searchByTitle(String title) {
        return productRepository.findByTitleContaining(title);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}