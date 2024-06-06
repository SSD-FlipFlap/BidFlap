package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    Optional<ChatRoom> getChatRoomById(long chatRoomId);

    List<ChatRoom> findChatRoomByProductIdAndNickname(long productId, String nickname);

    List<ChatRoom> findByAfterServiceIdAndNickname(long afterServiceId, String nickname);

    List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    ChatRoom insertChatRoom(String type, long id, Member member);

    void deleteChatRoom(Long chatRoomId);

    ChatMessage insertMessage(ChatMessage message);

    List<ChatRoom> getProductChatRoomListByNickname(String nickname);

    List<ChatRoom> getAsChatRoomListByNickname(String nickname);

    ChatMessage createMessage(Long roomId, Member member, String message);

    List<ChatRoom> findByProductId(long pId);

    List<ChatRoom> findByAfterServiceId(long asId);

    //void deleteMessages(int chatRoomId);

}
