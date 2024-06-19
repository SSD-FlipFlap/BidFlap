package com.ssd.bidflap.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_seq_generator")
    @SequenceGenerator(name = "chat_room_seq_generator", sequenceName = "CHAT_ROOM_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "after_service_id")
    private AfterService afterService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @PreRemove
    public void deleteFiles(){
        for(ChatMessage message : chatMessageList){
            if(message.getAttachmentUrl() != null){
                File file = new File(message.getAttachmentUrl());
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }

    public static ChatRoom createRoom() {
        return ChatRoom.builder()
                .build();
    }
}
