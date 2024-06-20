package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface ChatService {
    Optional<ChatRoom> getChatRoomById(long chatRoomId);

    List<ChatRoom> findByProductIdAndNickname(long productId, String nickname);

    List<ChatRoom> findByAfterServiceIdAndNickname(long afterServiceId, String nickname);

    List<ChatMessage> getChatMessagesByChatRoomId(long chatRoomId);

    ChatRoom insertChatRoom(String type, long id, Member member);

    void deleteChatRoom(Long chatRoomId);

    List<ChatRoom> getProductChatRoomListByNickname(String nickname);

    List<ChatRoom> getAsChatRoomListByNickname(String nickname);

    ChatMessage createMessage(Long roomId, Member member, String message, String attachmentUrl);

    List<ChatRoom> findByProductId(long pId);

    List<ChatRoom> findByAfterServiceId(long asId);

    String saveAttachment(MultipartFile attachment) throws IOException;

}
