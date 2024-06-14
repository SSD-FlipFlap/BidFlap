package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Bidder;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.repository.AuctionRepository;
import com.ssd.bidflap.repository.BidderRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidderRepository bidderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //경매시작
    @Transactional
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
                .createdAt(LocalDateTime.now())
                .period(duePeriod)
                .productId(product.getId())
                .highPrice(product.getPrice())
                .build();


        product.setAuction(auction);
        product.setStatus(ProductStatus.AUCTION);
        auctionRepository.save(auction);
        productRepository.save(product);
    }

    //입찰
    @Transactional
    public void placeBid(Long productId, int bidPrice, String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        System.out.println("Optional<Member> 값: " + optionalMember);

        if (optionalMember.isEmpty()) {
            System.out.println("사용자를 찾을 수 없습니다.");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        } else {
            System.out.println("사용자를 찾았습니다.");
        }

        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Auction auction = product.getAuction();
        if (auction == null) {
            throw new IllegalArgumentException("해당 상품은 경매가 시작되지 않았습니다.");
        }
        //보증금 제도
        boolean alreadyBid = bidderRepository
                .existsByAuctionAndMember(auction, member);

        int depositAmount = alreadyBid ? 0: (int)(0.5*bidPrice);

        if (depositAmount > 0) {
            if (member.getDepositBalance() < depositAmount) {
                throw new IllegalArgumentException("보증금 잔액이 부족합니다.");
            }
            member.setDepositBalance(member.getDepositBalance() - depositAmount);
            memberRepository.save(member);
        }

        Bidder bidder = Bidder.builder()
                .price(bidPrice)
                .deposit(depositAmount)
                .member(member)
                .auction(auction)
                .build();
        bidderRepository.save(bidder);


        auction.updateHighPrice(bidPrice);
        auctionRepository.save(auction);

        messagingTemplate.convertAndSend("/bid", bidPrice);
    }
//    public List<Bidder> getBiddersByAuctionId(Long auctionId) {
//        return bidderRepository.findByAuctionId(auctionId);
//    }

}
