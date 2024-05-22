package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    public List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    public ChatMessage save(ChatMessage chatMessage);

    public void deleteByChatRoomId(int chatRoomId);
}
