package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByMember(Member member);
}
