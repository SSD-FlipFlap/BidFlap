package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    //Optional<Product> findById(Long aLong);

    //Optional<Product> findById(long productId);
    List<Product> findByTitleContaining(String keyword);

    List<Product> findByMember(Member member);

    List<Product> findByMemberAndStatus(Member member, ProductStatus status);

    int countByMemberAndStatus(Member member, ProductStatus productStatus);


    List<Product> findByCategory(Category category);

    List<Product> findAllByOrderByLikeCountDesc();

    List<Product> findByCategoryInOrderByLikeCountDesc(List<Category> categories);

    List<Product> findProductsByCategoryOrderByCreatedAtDesc(Category category);

    List<Product> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("UPDATE Product p SET p.member = :unknownMember WHERE p.member = :member")
    void updateMemberToUnknown(@Param("member") Member member, @Param("unknownMember") Member unknownMember);
}
