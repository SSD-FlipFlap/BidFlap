package com.ssd.bidflap.service.impl;

import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.repository.*;
import com.ssd.bidflap.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final AfterServiceRepository afterServiceRepository;
    private final MemberRepository memberRepository;

    public ChatRoom getChatRoomById(long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }

    @Override
    public Optional<ChatRoom> getChatRoomByProductIdAndNickname(long productId, String nickname) {
        return chatRoomRepository.findByProductIdAndNickname(productId, nickname);
    }

    @Override
    public Optional<ChatRoom> getChatRoomByAfterServiceIdAndNickname(long afterServiceId, String nickname) {
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
        }
        Optional<AfterService> optionalAfterService = afterServiceRepository.findById(id);
        ChatRoom chatRoom = ChatRoom.builder()
                .afterService(optionalAfterService.get())
                .build();
        return chatRoomRepository.save(chatRoom);
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
    public List<ChatRoom> getProductChatRoomListByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findByMember(member.get());
        List<ChatRoom> productChatRooms = chatMessages.stream()
                .filter(chatMessage -> chatMessage.getChatRoom().getProduct() != null)
                .map(ChatMessage::getChatRoom)
                .distinct()
                .collect(Collectors.toList());
        return productChatRooms;
    }

    @Override
    public List<ChatRoom> getAsChatRoomListByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findByMember(member.get());
        List<ChatRoom> asChatRooms = chatMessages.stream()
                .filter(chatMessage -> chatMessage.getChatRoom().getAfterService() != null)
                .map(ChatMessage::getChatRoom)
                .distinct()
                .collect(Collectors.toList());
        return asChatRooms;
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
