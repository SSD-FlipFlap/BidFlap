package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage extends BaseEntity implements Comparable<ChatMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private ReadStatus isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    public ChatMessage(ChatRoom room, Member sender, String message) {
        super();
        this.chatRoom = room;
        this.member = sender;
        this.message = message;
    }

    public static ChatMessage createChatMessage(ChatRoom room, Member sender, String message) {
        return ChatMessage.builder()
                .chatRoom(room)
                .member(sender)
                .message(message)
                .build();
    }

    @Override
    public int compareTo(ChatMessage o) {
        return this.getCreatedAt().compareTo(o.getCreatedAt());
    }
}
