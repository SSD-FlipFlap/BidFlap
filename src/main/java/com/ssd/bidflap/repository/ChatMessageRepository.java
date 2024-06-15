package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    List<ChatMessage> findByMember(Member member);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.member = :unknownMember WHERE cm.member = :member")
    void updateMemberToUnknown(@Param("member") Member member, @Param("unknownMember") Member unknownMember);
}
