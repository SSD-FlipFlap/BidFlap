package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpSession httpSession;

    public void registerProduct(Product product,String nickname/*, MultipartFile product_filepath*/) {
//        String productImgPath= System.getProperty("user.dir")+ "\\src\\main\\resources\\static\\productImg";
//
//        UUID uuid = UUID.randomUUID();
//
//        String fileName= uuid+ "_"+product_filepath.getOriginalFilename();
//
//        File saveFile=new File(productImgPath, fileName);
//
//        product_filepath.transferTo(saveFile);

        // 로그인한 회원의 닉네임으로 가져옴
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member persistedMember = optionalMember.get();

        // 상품에 로그인한 회원의 정보 설정
        product.setMember(persistedMember);
        productRepository.save(product);
    }

    /*글 등록 후 id를 통해서 바로 글 상세페이지를 보기 위한*/
    public Long getProductId(Product product) {
        Product savedProduct= productRepository.save(product);
        return savedProduct.getId();
    }

    public Product productView(Long id){
        return productRepository.findById(id).get();
    }

    public void productDelete(Long id){
        productRepository.deleteById(id);
    }

    public List<Product> searchByTitle(String title) {
        return productRepository.findByTitleContaining(title);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

}
