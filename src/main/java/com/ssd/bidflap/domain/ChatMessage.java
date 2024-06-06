package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage extends BaseEntity implements Comparable<ChatMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_message_seq_generator")
    @SequenceGenerator(name = "chat_message_seq_generator", sequenceName = "CHAT_MESSAGE_SEQ", allocationSize = 1)
    private Long id;

    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    private ReadStatus isRead = ReadStatus.NOT_READ;

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

    public void updateReadStatus(ReadStatus readStatus) {
        this.isRead = readStatus;
    }
}
