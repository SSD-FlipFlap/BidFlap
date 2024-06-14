package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Bidder;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidderRepository extends JpaRepository<Bidder, Long> {
    boolean existsByAuctionAndMember(Auction auction, Member member);
}
