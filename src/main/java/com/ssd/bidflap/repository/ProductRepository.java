package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findById(long productId);
    List<Product> findByTitleContaining(String keyword);
}
