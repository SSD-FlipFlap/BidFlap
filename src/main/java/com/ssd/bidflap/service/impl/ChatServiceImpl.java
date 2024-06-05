package com.ssd.bidflap.service.impl;

import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.repository.*;
import com.ssd.bidflap.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

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

    public Optional<ChatRoom> getChatRoomById(long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }

    @Override
    public List<ChatRoom> findChatRoomByProductIdAndNickname(long productId, String nickname) {
        return chatRoomRepository.findByProductIdAndNickname(productId, nickname);
    }

    @Override
    public List<ChatRoom> findByAfterServiceIdAndNickname(long afterServiceId, String nickname) {
        return chatRoomRepository.findByAfterServiceIdAndNickname(afterServiceId, nickname);
    }

    @Override
    public List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId) {
        List<ChatMessage> cmList = chatMessageRepository.getChatMessagesByChatRoomId(chatRoomId);
        Collections.sort(cmList);

        return cmList;
    }

    @Override
    public ChatRoom insertChatRoom(String type, long id, Member member) {
        if(type.equals("product")) {
            Optional<Product> optionalProduct = productRepository.findById(id);

            if(optionalProduct.isEmpty())
                throw new NotFoundException("판매글이 없습니다.");

            ChatRoom chatRoom = ChatRoom.builder()
                    .product(optionalProduct.get())
                    .member(member)
                    .build();
            return chatRoomRepository.save(chatRoom);
        }

        Optional<AfterService> optionalAfterService = afterServiceRepository.findById(id);
        ChatRoom chatRoom = ChatRoom.builder()
                .afterService(optionalAfterService.get())
                .member(member)
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public void deleteChatRoom(Long chatRoomId) {
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
        Optional<ChatRoom> room = getChatRoomById(roomId);
        ChatMessage mm = new ChatMessage(room.get(), member, message);
        mm.getCreatedAt();
        return chatMessageRepository.save(mm);
    }

    @Override
    public List<ChatRoom> findByProductId(long pId) {
        return chatRoomRepository.findByProductId(pId);
    }

    @Override
    public List<ChatRoom> findByAfterServiceId(long asId) {
        return chatRoomRepository.findByAfterServiceId(asId);
    }
    /*
    @Override
    public void deleteMessages(int chatRoomId) {
        chatMessageRepository.deleteByChatRoomId(chatRoomId);
    }
    */

}
