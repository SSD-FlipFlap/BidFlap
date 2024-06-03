package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findById(long productId);
    List<Product> findByTitleContaining(String keyword);

    List<Product> findByMember(Member member);

    List<Product> findByMemberAndStatus(Member member, ProductStatus status);
}
