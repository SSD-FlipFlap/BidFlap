package com.ssd.bidflap.service;


import com.ssd.bidflap.domain.Auction;
import com.ssd.bidflap.domain.Bidder;
import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.enums.NotificationType;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    @Autowired
    private ProductLikeRepository productLikeRepository;
    @Autowired
    private NotificationService notificationService;


    //경매시작
    @Transactional
    public void startAuction(Long id, int duePeriod){
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("올바르지 않은 상품 아이디"));
        //좋아요 10개 이하면, 경매 불가능
//        if (product.getLikeCount() < 10) {
//            throw new IllegalStateException("경매 시작 개수 도달 x");
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


        //경매 시작 -> 좋아요 누른 사람들에게 notification
        List<ProductLike> productLikes = productLikeRepository.findByProduct(product);
        notificationService.createAuctionNotifications(productLikes);

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

        Member member = optionalMember.get();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Auction auction = product.getAuction();
        if (auction == null) {
            throw new IllegalArgumentException("해당 상품은 경매가 시작되지 않았습니다.");
        }

        //최고금액
        if (bidPrice <= auction.getHighPrice()) {
            throw new IllegalArgumentException("현재 최고 금액보다 높은 입찰 금액을 입력해야 합니다.");
        }
        
        //보증금 제도
        boolean alreadyBid = auction.getBidderList().stream()
                .anyMatch(bidder -> bidder.getMember().equals(member));

        //보증금 현재 50%
        int depositAmount = alreadyBid ? 0: (int)(0.5*product.getPrice());

        if (depositAmount > 0) {
            if (member.getDepositBalance() < depositAmount) {
                throw new IllegalArgumentException("보증금 잔액 부족");
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
        auction.getBidderList().add(bidder);
        auction.updateHighPrice(bidPrice);
        auction.setSuccessfulBidder(member.getId());
        auctionRepository.save(auction);

        messagingTemplate.convertAndSend("/bid", bidPrice);
    }

    
    //경매종료
    @Transactional  
    public void endAuction(Long id){
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 아이디에 따른 경매가 없습니다."));

        if(!auction.isAuctionEnded()){
            throw new IllegalArgumentException("경매기간이 아직 남았습니다.");
        }

        Product product = productRepository.findById(auction.getProductId()).orElseThrow(() -> new IllegalArgumentException("올바르지 않은 상품 아이디"));

        List<Bidder> sortedBidders = auction.getBidderList().stream()
                .sorted(Comparator.comparing(Bidder::getPrice).reversed())
                .toList();

        if (!sortedBidders.isEmpty()) {
            Bidder highestBidder = sortedBidders.get(0);
            product.setFinalBidPrice(highestBidder.getPrice());
        }

        if (auction.getSuccessfulBidder()!= null){
            product.setStatus(ProductStatus.AUCTION_WON);
        }else{
            //낙찰자 없거나, 경매 참여 인원 없을때
            product.setStatus(ProductStatus.AUCTION_ENDED);
        }


    }

    //구매 취소
    @Transactional
    public void cancelAuctionPurchase(Long id, String nickname) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디에 따른 경매가 없습니다."));

        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!member.getId().equals(auction.getSuccessfulBidder())) {
            throw new IllegalArgumentException("해당 사용자는 낙찰자가 아닙니다.");
        }

        List<Bidder> sortedBidders = auction.getBidderList().stream()
                .sorted(Comparator.comparing(Bidder::getPrice).reversed())
                .toList();

        Optional<Bidder> nextBidderOptional = sortedBidders.stream()
                .filter(bidder -> !bidder.getMember().getId().equals(member.getId()))
                .findFirst();

        if (nextBidderOptional.isPresent()) {
            Bidder nextBidder = nextBidderOptional.get();
            auction.setSuccessfulBidder(nextBidder.getMember().getId());
        } else {
            auction.setSuccessfulBidder(null);
        }

        auctionRepository.save(auction);
    }

    //구매 확정
//    @Transactional
//    public void confirmAuctionPurchase(Long auctionId, String nickname) {
//        Auction auction = auctionRepository.findById(auctionId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 아이디에 따른 경매가 없습니다."));
//
//        Member member = memberRepository.findByNickname(nickname)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        if (!member.getId().equals(auction.getSuccessfulBidder())) {
//            throw new IllegalArgumentException("해당 사용자는 낙찰자가 아닙니다.");
//        }
//
//        Product product = productRepository.findById(auction.getProductId())
//                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
//
//        product.setStatus(ProductStatus.AUCTION_ENDED);
//
//        // 보증금 반환
//        auction.getBidderList().stream()
//                .filter(bidder -> !bidder.getMember().getId().equals(auction.getSuccessfulBidder()))
//                .forEach(bidder -> {
//                    Member memberToRefund = bidder.getMember();
//                    memberToRefund.setDepositBalance(memberToRefund.getDepositBalance() + bidder.getDeposit());
//                    memberRepository.save(memberToRefund);
//                });
//
//        // 낙찰자의 보증금 반환
//        auction.getBidderList().stream()
//                .filter(bidder -> bidder.getMember().getId().equals(auction.getSuccessfulBidder()))
//                .findFirst()
//                .ifPresent(bidder -> {
//                    Member winner = bidder.getMember();
//                    winner.setDepositBalance(winner.getDepositBalance() + bidder.getDeposit());
//                    memberRepository.save(winner);
//                });
//
//        auctionRepository.save(auction);
//        productRepository.save(product);
//    }

    // 낙찰된 경매 내역 조회
    public List<Product> getProductsByMemberIdAndStatus(String nickname, ProductStatus status) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return bidderRepository.findProductsByMemberIdAndStatus(member.getId(), status);
    }

    // 진행 중인 or 완료된 경매 내역 조회
    public List<Product> getAuctionWonProductsByMemberIdAndStatus(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return bidderRepository.getProductsByMemberId(member.getId());
    }

    // 모든 경매 내역 조회
    public List<Product> getAllProductsByMemberId(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return bidderRepository.getAllProductsByMemberId(member.getId());
    }

    // 참여 중인 경매 or 완료된 경매 개수
    public int countAuctionProductsByMemberIdAndStatus(String nickname, ProductStatus status) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return bidderRepository.countAuctionProductsByMemberIdAndStatus(member.getId(), status);
    }

    // 낙찰된 경매 개수
    public int countSuccessfulBidProductsByMember(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return bidderRepository.countSuccessfulBidProductsByMemberId(member.getId());
    }
}
