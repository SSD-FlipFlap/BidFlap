package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.domain.enums.NotificationType;
import com.ssd.bidflap.domain.enums.ReadStatus;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Notification> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByMemberIdAndReadStatus(memberId, ReadStatus.NOT_READ);
    }

    public boolean hasUnreadNotifications(String nickname) {//불이미지 띄울 때 사용
        return memberRepository.findByNickname(nickname)
                .map(member -> !notificationRepository.findByMemberIdAndReadStatus(member.getId(), ReadStatus.NOT_READ).isEmpty())
                .orElse(false);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadStatus(ReadStatus.READ);
        notificationRepository.save(notification);
    }

    public Notification createChatNotification(ChatMessage chatMessage, Member receiver) {
        Notification notification = Notification.builder()
                .message("새로운 채팅 메시지가 도착했습니다.")
                .readStatus(ReadStatus.NOT_READ)
                .notificationType(NotificationType.CHAT)
                .member(receiver)
                .product(chatMessage.getChatRoom().getProduct())
                .chatRoom(chatMessage.getChatRoom())
                .build();
        return notificationRepository.save(notification);
    }

    public void createAuctionNotifications(List<ProductLike> productLikes) {
        for (ProductLike productLike : productLikes) {
            Notification notification = Notification.builder()
                    .message("등록자가 경매를 시작하였습니다.")
                    .readStatus(ReadStatus.NOT_READ)
                    .notificationType(NotificationType.AUCTION)
                    .member(productLike.getMember())
                    .product(productLike.getProduct())
                    .build();
            notificationRepository.save(notification);
        }
    }


    @Transactional
    public void createAuctionWonNotification(Auction auction, Product product, Member successfulBidder) {
        Notification notification = Notification.builder()
                .member(successfulBidder)
                .message("축하합니다! " + product.getTitle() + " 경매에 낙찰되었습니다.")
                .readStatus(ReadStatus.NOT_READ)
                .notificationType(NotificationType.AUCTION_WON)
                .product(product)
                .build();
        notificationRepository.save(notification);

        Notification sellerNotification = Notification.builder()
                .member(product.getMember())
                .message("상품 " + product.getTitle() + "가 경매에 낙찰되었습니다.")
                .readStatus(ReadStatus.NOT_READ)
                .notificationType(NotificationType.AUCTION_WON)
                .product(product)
                .build();
        notificationRepository.save(sellerNotification);
    }
}
