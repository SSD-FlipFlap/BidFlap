package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.enums.ProductStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
}