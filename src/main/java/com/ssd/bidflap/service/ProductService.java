package com.ssd.bidflap.service;

import com.ssd.bidflap.config.aws.AmazonS3Manager;
import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductLikeRepository productLikeRepository; // 추가: ProductLikeRepository 의존성 주입
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;
    private final ProductImageRepository productImageRepository;

    public Product registerProduct(Product product, List<MultipartFile> files, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Member persistedMember = optionalMember.get();
        product.setMember(persistedMember);
        product.setStatus(ProductStatus.SELLING);   // 초기 상태 설정

        registerProductImages(product, files);  // 이미지 저장

        return productRepository.save(product);
    }

    public Product productView(Long id) {
        return productRepository.findById(id).orElse(null); // 변경: Optional.get() 대신 Optional.orElse(null) 사용
    }

    public void productDelete(Long id, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("해당하는 상품을 찾을 수 없습니다.");
        }
        Product product = optionalProduct.get();

        // 글 작성자인지 확인
        if (!(optionalMember.get().equals(product.getMember()))) {
            throw new IllegalStateException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        // S3에서 이미지 삭제
        List<String> imageUrls = product.getProductImageList().stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());
        deleteProductImagesFromS3(imageUrls);

        productRepository.deleteById(product.getId());
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

    public void startAuction(Long productId, String nickname) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
//        product.setStatus(AuctionStatus.ACTIVE); // 경매 상태로 변경
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

    public void updateProduct(Long id, Product updatedProduct, List<MultipartFile> files, List<String> removedImageUrls, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Product productTemp = productView(id);
        if (productTemp == null) {
            throw new IllegalArgumentException("해당하는 상품을 찾을 수 없습니다.");
        }

        // 글 작성자인지 확인
        if (!(optionalMember.get().equals(productTemp.getMember()))) {
            throw new IllegalStateException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        // 정보 업데이트
        productTemp.setTitle(updatedProduct.getTitle());
        productTemp.setDescription(updatedProduct.getDescription());
        productTemp.setPrice(updatedProduct.getPrice());
        productTemp.setCategory(updatedProduct.getCategory());


        if (removedImageUrls != null && !removedImageUrls.isEmpty()) {
            // 기존 이미지 s3에서 삭제
            deleteProductImagesFromS3(removedImageUrls);

            // db에서 삭제
            List<ProductImage> imagesToDelete = productImageRepository.findByUrlIn(removedImageUrls);
            productImageRepository.deleteAll(imagesToDelete);
        }
        // 새로운 이미지 등록
        registerProductImages(productTemp, files);

        // 변경된 정보 저장
        productRepository.save(productTemp);
    }

    public void deleteProductImagesFromS3(List<String> removedImageUrls) {
        for (String image : removedImageUrls) {
            s3Manager.deleteFile(image);
        }
    }

    // 이미지 저장
    private void registerProductImages(Product product, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            // 파일 이름을 기준으로 중복 제거
            List<MultipartFile> uniqueFiles = files.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    MultipartFile::getOriginalFilename,
                                    file -> file,
                                    (existing, replacement) -> existing // 동일한 이름을 가진 파일이 있다면 처음 발견된 파일을 사용
                            ),
                            map -> new ArrayList<>(map.values())
                            ));

            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : uniqueFiles) {
                if (!file.isEmpty()) {  // 각 항목이 유효한 파일을 포함하고 있는지 확인(파일 선택 안 한 경우에도 1개 전송됨)
                    String uuid = UUID.randomUUID().toString();
                    Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
                    String pictureUrl = s3Manager.uploadFile(s3Manager.generateProductKeyName(savedUuid), file);

                    ProductImage productImage = ProductImage.builder()
                            .url(pictureUrl)
                            .product(product)
                            .build();

                    productImages.add(productImage);
                }
            }
            product.setProductImageList(productImages);
        }
    }

    public List<Product> getProductByMember(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return productRepository.findByMember(member);
    }

    public List<Product> getProductByMemberAndStatus(String nickname, String status) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        ProductStatus productStatus = ProductStatus.valueOf(status.trim().toUpperCase());
        return productRepository.findByMemberAndStatus(member, productStatus);
    }

    public List<ProductLike> getProductLikeByMember(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return productLikeRepository.findByMember(member);
    }
}