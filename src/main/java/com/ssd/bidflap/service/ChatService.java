package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    ChatRoom getChatRoomById(long chatRoomId);

    Optional<ChatRoom> getChatRoomByProductIdAndNickname(long productId, String nickname);

    Optional<ChatRoom> getChatRoomByAfterServiceIdAndNickname(long afterServiceId, String nickname);

    List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    ChatRoom insertChatRoom(String type, long id);

    void deleteChatRoom(Long chatRoomId);

    ChatMessage insertMessage(ChatMessage message);

    List<ChatRoom> getProductChatRoomListByNickname(String nickname);

    List<ChatRoom> getAsChatRoomListByNickname(String nickname);

    ChatMessage createMessage(Long roomId, Member member, String message);

    //void deleteMessages(int chatRoomId);

}
