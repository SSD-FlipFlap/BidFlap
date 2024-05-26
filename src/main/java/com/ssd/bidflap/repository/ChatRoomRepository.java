package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    public ChatRoom findById(long chatRoomId);

    public ChatRoom save(ChatRoom chatRoom);

    public void deleteById(long id);

    public List<ChatRoom> getChatRoomListByProductId(long productId);

    @Query("SELECT DISTINCT cm.chatRoom FROM ChatMessage cm WHERE cm.member.nickname = :nickname")
    public List<ChatRoom> getChatRoomListByNickname(String nickname);

}
