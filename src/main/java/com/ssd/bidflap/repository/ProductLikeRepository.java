package com.ssd.bidflap.repository;


import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByMemberAndProduct(Member member, Product product);

    List<ProductLike> findByMember(Member member);
}
