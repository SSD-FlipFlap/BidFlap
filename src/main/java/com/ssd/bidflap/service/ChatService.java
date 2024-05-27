package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;

import java.util.List;

public interface ChatService {
    ChatRoom getChatRoomById(long chatRoomId);

    ChatRoom getChatRoomByProductIdAndNickname(long productId, String nickname);

    ChatRoom getChatRoomByAfterServiceIdAndNickname(long afterServiceId, String nickname);

    List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    ChatRoom insertChatRoom(String type, long id);

    void deleteChatRoom(long chatRoomId);

    ChatMessage insertMessage(ChatMessage message);

    List<ChatRoom> getChatRoomListByNickname(String nickname);

    ChatMessage createMessage(Long roomId, Member member, String message);

    //void deleteMessages(int chatRoomId);

}
