package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    public ChatRoom findById(long chatRoomId);

    @Query("SELECT cr FROM ChatRoom cr JOIN ChatMessage cm ON cr.id = cm.chatRoom.id " +
            "WHERE cr.afterService.id = :afterServiceId AND cm.member.nickname = :nickname")
    public ChatRoom findByAfterServiceIdAndNickname(long afterServiceId, String nickname);
    // product_id로 chatroom list 찾는다
    // 그중 nickname으로 알맞은 chatroom을 찾는다.

    @Query("SELECT DISTINCT cm.chatRoom FROM ChatRoom cr JOIN ChatMessage cm ON cr.id = cm.chatRoom.id WHERE cr.product.id = :productId AND cm.member.nickname = :nickname")
    public ChatRoom findByProductIdAndNickname(long productId, String nickname);

    public ChatRoom save(ChatRoom chatRoom);

    public void deleteById(long id);

    @Query("SELECT DISTINCT cm.chatRoom FROM ChatMessage cm WHERE cm.member.nickname = :nickname")
    public List<ChatRoom> getChatRoomListByNickname(String nickname);
}
