package com.ssd.bidflap.Scheduler;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.service.AuctionService;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.repository.AuctionRepository;
import com.ssd.bidflap.repository.ProductRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;




import java.util.List;

@Component
public class AuctionScheduler {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void EndAuctionScheduler() {
        List<Product> ongoingAuction = productRepository.findAllByStatus(ProductStatus.AUCTION);

        for (Product product : ongoingAuction) {
            if (product.getAuction() != null) {
                Hibernate.initialize(product.getAuction());
                if (product.getAuction().isAuctionEnded()) {
                    auctionService.endAuction(product.getAuction().getId());
                }
            }
        }
    }
}
