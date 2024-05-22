package com.ssd.bidflap.service.impl;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.repository.ChatMessageRepository;
import com.ssd.bidflap.repository.ChatRoomRepository;
import com.ssd.bidflap.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;

    @Override
    public List<ChatRoom> getChatRoomList(long productId) {
        return chatRoomRepository.getChatRoomListByProductId(productId);
    }

    @Override
    public ChatRoom getChatRoomById(long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }

    @Override
    public List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId) {
        List<ChatMessage> cmList = chatMessageRepository.getChatMessagesByChatRoomId(chatRoomId);
        Collections.sort(cmList);

        return cmList;
    }

    @Override
    public void insertChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public void deleteChatRoom(long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

    @Override
    public ChatMessage insertMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatRoom> getChatRoomListByMemberId(long memberId) {
        return chatRoomRepository.getChatRoomListByMemberId(memberId);
    }

    @Override
    public ChatMessage createMessage(Long roomId, Member member, String message) {
        ChatRoom room = getChatRoomById(roomId);
        ChatMessage mm = new ChatMessage(room, member, message);
        return chatMessageRepository.save(mm);
    }
    /*
    @Override
    public void deleteMessages(int chatRoomId) {
        chatMessageRepository.deleteByChatRoomId(chatRoomId);
    }
    */

}
