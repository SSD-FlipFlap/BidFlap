package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Bidder;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.enums.AuctionStatus;
import com.ssd.bidflap.repository.AuctionRepository;
import com.ssd.bidflap.repository.BidderRepository;
import com.ssd.bidflap.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {


    private ProductRepository productRepository;
    private AuctionRepository auctionRepository;
    private BidderRepository bidderRepository;

    @Autowired
    public AuctionService(ProductRepository productRepository, AuctionRepository auctionRepository) {
        this.productRepository = productRepository;
        this.auctionRepository = auctionRepository;
    }

    //경매시작
    public void startAuction(Long id, int duePeriod){
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        //좋아요 10개 이하면, 경매 불가능
//        if (product.getLikeCount() < 10) {
//            throw new IllegalStateException("at least like 10 ");
//        }
        //경매종료날짜
        LocalDateTime dueDate = LocalDateTime.now().plusDays(duePeriod);
        //경매 객체 생성
        Auction auction = Auction.builder()
                .period(duePeriod)
                .highPrice(product.getPrice())
                .build();

        product.setAuction(auction);
        product.setStatus(AuctionStatus.STARTED);
        auctionRepository.save(auction);
        productRepository.save(product);

    }
}
