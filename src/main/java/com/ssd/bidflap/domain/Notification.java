package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.NotificationType;
import com.ssd.bidflap.domain.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_generator")
    @SequenceGenerator(name = "notification_seq_generator", sequenceName = "NOTIFICATION_SEQ", allocationSize = 1)
    private Long id;

    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus = ReadStatus.NOT_READ;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;  // 경매, 낙찰, 채팅 알림 구분

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
}
