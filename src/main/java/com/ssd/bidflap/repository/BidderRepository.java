package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Bidder;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidderRepository extends JpaRepository<Bidder, Long> {

    @Query("SELECT p FROM Product p JOIN p.auction a JOIN a.bidderList b WHERE b.member.id = :memberId AND p.status = :status")
    List<Product> findProductsByMemberIdAndStatus(Long memberId, ProductStatus status);

    @Query("SELECT p FROM Product p JOIN p.auction a WHERE a.successfulBidder = :memberId")
    List<Product> getProductsByMemberId(Long memberId);

    @Query("SELECT p FROM Product p JOIN p.auction a JOIN a.bidderList b WHERE b.member.id = :memberId ")
    List<Product> getAllProductsByMemberId(Long memberId);

    // 특정 멤버의 경매중인/경매완료된 참여 상품 개수
    @Query("SELECT COUNT(p) FROM Product p JOIN p.auction a JOIN a.bidderList b WHERE b.member.id = :memberId AND p.status = :status")
    int countAuctionProductsByMemberIdAndStatus(Long memberId, ProductStatus status);

    // 특정 멤버의 낙찰된 상품 개수
    @Query("SELECT COUNT(p) FROM Product p JOIN p.auction a WHERE a.successfulBidder = :memberId")
    int countSuccessfulBidProductsByMemberId(Long memberId);
}