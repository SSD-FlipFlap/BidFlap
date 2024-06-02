package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByUrlIn(List<String> urls);
}