package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.dto.ChatMessageDto;
import com.ssd.bidflap.service.ChatService;
import com.ssd.bidflap.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final MemberService memberService;

    //채팅방리스트 - 마이페이지
    @GetMapping("/chatRooms")
    public ModelAndView getChatRoomList(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        String nickname = (String) session.getAttribute("loggedInMember");

        List<ChatRoom> chatRooms = chatService.getChatRoomListByNickname(nickname);
        modelAndView.addObject("chatRooms", chatRooms);

        modelAndView.setViewName("chat/chatRoomList");
        return modelAndView;
    }
    //입장
    @GetMapping("/chatRoom/{chatRoomId}")
    public ModelAndView getChatRoomById(@PathVariable long chatRoomId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        //chatRoom, chatRoomMessages, sender
        ChatRoom chatRoom = chatService.getChatRoomById(chatRoomId);
        modelAndView.addObject("chatRoom", chatRoom);

        List<ChatMessage> chatMessages=chatService.getChatMessagesByChatRoomId(chatRoomId);
        modelAndView.addObject("chatMessages", chatMessages);

        String nickname = (String) session.getAttribute("loggedInMember");
        Member sender = memberService.getMemberInfo(nickname);
        modelAndView.addObject("sender", sender);

        modelAndView.setViewName("chat/chatRoom");
        //modelAndView.setViewName("thyme/chat/chatRoom");

        return modelAndView;
    }

    //생성
    @PostMapping("/createChatRoom")
    public ModelAndView createChatRoom(@ModelAttribute ChatRoom chatRoom) {
        ModelAndView modelAndView = new ModelAndView();
        chatService.insertChatRoom(chatRoom);
        modelAndView.addObject("message", "Chat room created successfully");
        modelAndView.setViewName("chat/chatRoom");

        return modelAndView;
    }

    //채팅방 삭제
    @PostMapping("/deleteChatRoom/{chatRoomId}")
    public ModelAndView deleteChatRoom(@PathVariable long chatRoomId) {
        ModelAndView modelAndView = new ModelAndView();
        chatService.deleteChatRoom(chatRoomId);
        modelAndView.addObject("message", "Chat room deleted successfully");

        modelAndView.setViewName("redirect:/chat/chatRooms");
        return modelAndView;
    }

    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessageDto sendMessage(@DestinationVariable Long roomId, ChatMessageDto message) {
        ChatMessage mm = chatService.createMessage(roomId, message.getMember(), message.getMessage());
        return ChatMessageDto.builder()
                .roomId(roomId)
                .member(message.getMember())
                .message(message.getMessage())
                .build();
    }
    /*
    @PostMapping("/delete/{chatRoomId}")
    public ModelAndView deleteMessage(@PathVariable int chatRoomId) {
        ModelAndView modelAndView = new ModelAndView();

        chatService.deleteMessages(chatRoomId);
        modelAndView.addObject("message", "Messages deleted successfully");
        modelAndView.setViewName("redirect:/chatRoom/" + chatRoomId);
        return modelAndView;
    }
    */
}
