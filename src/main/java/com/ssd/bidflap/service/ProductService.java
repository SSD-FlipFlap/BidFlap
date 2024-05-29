package com.ssd.bidflap.service;

import com.ssd.bidflap.config.aws.AmazonS3Manager;
import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.repository.ProductLikeRepository; // 추가: ProductLikeRepository import
import com.ssd.bidflap.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductLikeRepository productLikeRepository; // 추가: ProductLikeRepository 의존성 주입
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    public Product registerProduct(Product product, List<MultipartFile> files, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member persistedMember = optionalMember.get();

        // 이미지 업로드
        List<ProductImage> productImages = new ArrayList<>(); // 이미지 URL들을 저장할 리스트
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
                String pictureUrl = s3Manager.uploadFile(s3Manager.generateProductKeyName(savedUuid), file);

                // ProductImage 엔티티 생성 및 설정
                ProductImage productImage = ProductImage.builder()
                        .url(pictureUrl)
                        .product(product)
                        .build();

                // ProductImage 엔티티 리스트에 추가
                productImages.add(productImage);
            }
        }

        product.setMember(persistedMember);
        product.setProductImageList(productImages);
        return productRepository.save(product);
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