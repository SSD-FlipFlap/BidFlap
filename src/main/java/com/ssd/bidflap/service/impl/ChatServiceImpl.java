package com.ssd.bidflap.service.impl;

import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.repository.ChatMessageRepository;
import com.ssd.bidflap.repository.ChatRoomRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ProductRepository productRepository;

    public ChatRoom getChatRoomById(long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }

    @Override
    public ChatRoom getChatRoomByProductIdAndNickname(long productId, String nickname) {
        return chatRoomRepository.findByProductIdAndNickname(productId, nickname);
    }

    @Override
    public ChatRoom getChatRoomByAfterServiceIdAndNickname(long afterServiceId, String nickname) {
        return chatRoomRepository.findByAfterServiceIdAndNickname(afterServiceId, nickname);
    }

    @Override
    public List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId) {
        List<ChatMessage> cmList = chatMessageRepository.getChatMessagesByChatRoomId(chatRoomId);
        Collections.sort(cmList);

        return cmList;
    }

    @Override
    public ChatRoom insertChatRoom(String type, long id) {
        if(type.equals("product")) {
            Product product = productRepository.findById(id);
            ChatRoom chatRoom = ChatRoom.builder()
                    .product(product)
                    .build();
            return chatRoomRepository.save(chatRoom);
        }/*else {
            /*
            AfterService afterService = afterServiceRepository.findById(afterServiceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid after service ID: " + afterServiceId));
            ChatRoom chatRoom = ChatRoom.builder()
                    .afterService(afterService)
                    .build();
            return chatRoomRepository.save(chatRoom);


        }*/
        return null;
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
    public List<ChatRoom> getChatRoomListByNickname(String nickname) {
        return chatRoomRepository.getChatRoomListByNickname(nickname);
    }

    @Override
    public ChatMessage createMessage(Long roomId, Member member, String message) {
        ChatRoom room = getChatRoomById(roomId);
        ChatMessage mm = new ChatMessage(room, member, message);
        mm.getCreatedAt();
        return chatMessageRepository.save(mm);
    }
    /*
    @Override
    public void deleteMessages(int chatRoomId) {
        chatMessageRepository.deleteByChatRoomId(chatRoomId);
    }
    */

}
