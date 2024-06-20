package com.ssd.bidflap.service.impl;

import com.ssd.bidflap.config.aws.AmazonS3Manager;
import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.repository.*;
import com.ssd.bidflap.service.ChatService;
import com.ssd.bidflap.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final AfterServiceRepository afterServiceRepository;
    private final MemberRepository memberRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;
    private final NotificationService notificationService;
    
    public Optional<ChatRoom> getChatRoomById(long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }

    @Override
    public List<ChatRoom> findByProductIdAndNickname(long productId, String nickname) {
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
    public List<ChatRoom> getProductChatRoomListByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<ChatRoom> myChatRooms = chatRoomRepository.findByMemberAndProductIsNotNull(member.get());  // 내가 메시지를 보낸 채팅방
        List<ChatRoom> productChatRooms = chatRoomRepository.findByProductMember(member.get()); // 내가 메시지를 받은 채팅방

        List<ChatRoom> distinctChatRooms = Stream.concat(myChatRooms.stream(), productChatRooms.stream())
                .distinct()
                .collect(Collectors.toList());

        return distinctChatRooms;
    }

    @Override
    public List<ChatRoom> getAsChatRoomListByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<ChatRoom> myChatRooms = chatRoomRepository.findByMemberAndAfterServiceIsNotNull(member.get()); // 내가 메시지를 보낸 채팅방
        List<ChatRoom> asChatRooms = chatRoomRepository.findByAfterServiceMember(member.get()); // 내가 메시지를 받은 채팅방

        List<ChatRoom> distinctChatRooms = Stream.concat(myChatRooms.stream(), asChatRooms.stream())
                .distinct()
                .collect(Collectors.toList());

        return distinctChatRooms;
    }

    @Override
    public ChatMessage createMessage(Long roomId, Member member, String message, String attachmentUrl) {
        Optional<ChatRoom> room = getChatRoomById(roomId);
        ChatMessage mm;
        if(attachmentUrl ==null)
            mm = new ChatMessage(room.get(), member, message);
        else
            mm = new ChatMessage(room.get(), member, message, attachmentUrl);
        mm.getCreatedAt();

        createNotificationForMessage(mm, member);
        return chatMessageRepository.save(mm);
    }

    private void createNotificationForMessage(ChatMessage chatMessage, Member sender) {
        ChatRoom chatRoom = chatMessage.getChatRoom();
        Member notificationMember=null;

        System.out.println("Sender: " + sender + chatRoom.getMember());


        // 메시지를 보내는 사람이 chatRoom의 member인 경우 -> 받는 사람은 product or as
        if (chatRoom.getMember().equals(sender)) {
            // 글 구매하기의 경우 (product가 있는 경우)
            if (chatRoom.getProduct() != null) {
                notificationMember = chatRoom.getProduct().getMember();
            }
            // AS의 경우 (afterService가 있는 경우)
            else if (chatRoom.getAfterService() != null) {
                notificationMember = chatRoom.getAfterService().getMember();
            }
        }
        // 메시지를 보내는 사람이 chatRoom의 member가 아닌 경우
        else {
            // 알림을 받을 멤버를 chatRoom의 member로 설정
            notificationMember = chatRoom.getMember();
        }

        // 알림 생성
        notificationService.createChatNotification(chatMessage, notificationMember);
    }

    @Override
    public List<ChatRoom> findByProductId(long pId) {
        return chatRoomRepository.findByProductId(pId);
    }

    @Override
    public List<ChatRoom> findByAfterServiceId(long asId) {
        List<ChatRoom> crList = chatRoomRepository.findByAfterServiceId(asId);
        Collections.sort(crList);

        return crList;
    }

    @Override
    public String saveAttachment(MultipartFile file) {
        // 이미지 업로드
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());
            imageUrl = s3Manager.uploadFile(s3Manager.generateChatKeyName(savedUuid), file);
        }

        return  imageUrl;
    }
    /*
    @Override
    public void deleteMessages(int chatRoomId) {
        chatMessageRepository.deleteByChatRoomId(chatRoomId);
    }
    */

}
