package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    public List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    public void deleteByChatRoomId(int chatRoomId);

    List<ChatMessage> findByMember(Member member);
}
