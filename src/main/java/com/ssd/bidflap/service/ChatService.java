package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;

import java.util.List;

public interface ChatService {
    List<ChatRoom> getChatRoomList(long productId);

    ChatRoom getChatRoomById(long chatRoomId);

    List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    void insertChatRoom(ChatRoom chatRoom);

    void deleteChatRoom(long chatRoomId);

    ChatMessage insertMessage(ChatMessage message);

    List<ChatRoom> getChatRoomListByMemberId(long memberId);

    ChatMessage createMessage(Long roomId, Member member, String message);

    //void deleteMessages(int chatRoomId);

}
