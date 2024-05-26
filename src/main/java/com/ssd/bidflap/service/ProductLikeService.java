////package com.ssd.bidflap.service;
////
////import com.ssd.bidflap.domain.Member;
////import com.ssd.bidflap.domain.Product;
////import com.ssd.bidflap.domain.ProductLike;
////import com.ssd.bidflap.domain.dto.ProductLikeDto;
////import com.ssd.bidflap.repository.MemberRepository;
////import com.ssd.bidflap.repository.ProductLikeRepository;
////import com.ssd.bidflap.repository.ProductRepository;
////import jakarta.transaction.Transactional;
////import lombok.RequiredArgsConstructor;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
//package com.ssd.bidflap.service;
//
//import com.ssd.bidflap.domain.Member;
//import com.ssd.bidflap.domain.Product;
//import com.ssd.bidflap.domain.ProductLike;
//import com.ssd.bidflap.repository.MemberRepository;
//import com.ssd.bidflap.repository.ProductLikeRepository;
//import com.ssd.bidflap.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//public class ProductService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private ProductLikeRepository productLikeRepository;
//
//    @Transactional
//    public void toggleLike(Long productId, String nickname) {
//        // Find member by nickname
//        Member member = memberRepository.findByNickname(nickname)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Find product by id
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
//
//        // Check if the user has already liked the product
//        Optional<ProductLike> existingLike = productLikeRepository.findByMemberAndProduct(member, product);
//
//        // If the user has already liked the product, remove the like
//        if (existingLike.isPresent()) {
//            productLikeRepository.delete(existingLike.get());
//        } else {
//            // If the user has not liked the product, add the like
//            ProductLike productLike = ProductLike.builder()
//                    .member(member)
//                    .product(product)
//                    .build();
//            productLikeRepository.save(productLike);
//        }
//    }
//}
////
////import javax.persistence.NoResultException;
////import java.util.Optional;
////
////@Service
////@RequiredArgsConstructor
////public class ProductLikeService {
////    @Autowired
////    private MemberRepository memberRepository;
////    @Autowired
////    private ProductRepository productRepository;
////    @Autowired
////    private ProductLikeRepository productLikeRepository;
////
////    @Transactional
////    public void insert(ProductLikeDto productLikeDto) throws Exception {
////        Member member = memberRepository.findById(productLikeDto.getMemberId())
////                .orElseThrow(() -> new Exception("Member not found " + productLikeDto.getMemberId()));
////        Product product = productRepository.findById(productLikeDto.getProductId())
////                .orElseThrow(() -> new NoResultException("Product not found" + productLikeDto.getProductId()));
////
////        Optional<ProductLike> existingLike = productLikeRepository.findByMemberAndProduct(member, product);
////
////        if (existingLike.isPresent()) {
////            productLikeRepository.delete(existingLike.get());
////        } else {
////            ProductLike productLike = ProductLike.builder()
////                    .product(product)
////                    .member(member)
////                    .build();
////
////            productLikeRepository.save(productLike);
////        }
////    }
////}
