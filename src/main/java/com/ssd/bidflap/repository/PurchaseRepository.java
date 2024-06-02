package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Purchase findById(long purchaseId);
    Product findProductById(long productId);
}
