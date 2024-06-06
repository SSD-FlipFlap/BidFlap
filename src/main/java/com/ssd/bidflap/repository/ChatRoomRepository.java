package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.afterService.id = :afterServiceId AND cr.member.nickname = :nickname")
    public List<ChatRoom> findByAfterServiceIdAndNickname(long afterServiceId, String nickname);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr WHERE cr.product.id = :productId AND cr.member.nickname = :nickname")
    public List<ChatRoom> findByProductIdAndNickname(long productId, String nickname);

    List<ChatRoom> findByProductId(long pId);

    List<ChatRoom> findByAfterServiceId(long asId);
}
