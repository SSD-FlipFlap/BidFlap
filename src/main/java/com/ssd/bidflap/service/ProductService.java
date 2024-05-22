package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Transactional
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void registerProduct(Product product/*, MultipartFile product_filepath*/) {
//        String productImgPath= System.getProperty("user.dir")+ "\\src\\main\\resources\\static\\productImg";
//
//        UUID uuid = UUID.randomUUID();
//
//        String fileName= uuid+ "_"+product_filepath.getOriginalFilename();
//
//        File saveFile=new File(productImgPath, fileName);
//
//        product_filepath.transferTo(saveFile);

//        product.setCategory(Category.valueOf(product.getCategory()));
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
}
