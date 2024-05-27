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
    //입장 - product
    @GetMapping("/chatRoom/product/{productId}")
    public ModelAndView getChatRoomById(@PathVariable long productId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        //chatRoom, chatRoomMessages, sender
        String nickname = (String) session.getAttribute("loggedInMember");
        if(nickname == null)
            nickname = "rkfka";
        System.out.println(nickname+" "+productId);
        ChatRoom chatRoom = chatService.getChatRoomByProductIdAndNickname(productId, nickname);
        //System.out.println("chatRoom 찾음? "+chatRoom.getId());
        if(chatRoom==null){
            return createChatRoom("product", productId);
        }
        modelAndView.addObject("chatRoom", chatRoom);

        List<ChatMessage> chatMessages = chatService.getChatMessagesByChatRoomId(chatRoom.getId());
        //System.out.println(chatMessages.size()+" "+chatMessages.get(0).getMessage());
        modelAndView.addObject("chatMessages", chatMessages);

        Member sender = memberService.getMemberInfo(nickname);
        modelAndView.addObject("sender", sender);

        modelAndView.setViewName("chat/chatRoom");

        //modelAndView.setViewName("thyme/chat/chatRoom");

        return modelAndView;
    }
    //입장 - afterService
    @GetMapping("/chatRoom/afterService/{afterServiceId}")
    public ModelAndView getChatRoomByAfterServiceId(@PathVariable long afterServiceId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        //chatRoom, chatRoomMessages, sender
        String nickname = (String) session.getAttribute("loggedInMember");
        if(nickname == null)
            nickname = "rkfka";
        ChatRoom chatRoom = chatService.getChatRoomByAfterServiceIdAndNickname(afterServiceId, nickname);
        if(chatRoom==null)
            return createChatRoom("afterService", afterServiceId);    //생성
        modelAndView.addObject("chatRoom", chatRoom);

        List<ChatMessage> chatMessages=chatService.getChatMessagesByChatRoomId(chatRoom.getId());
        modelAndView.addObject("chatMessages", chatMessages);

        Member sender = memberService.getMemberInfo(nickname);
        modelAndView.addObject("sender", sender);

        modelAndView.setViewName("chat/chatRoom");

        //modelAndView.setViewName("thyme/chat/chatRoom");

        return modelAndView;
    }

    //생성
    private ModelAndView createChatRoom(String type, long id) {
        ModelAndView modelAndView = new ModelAndView();
        ChatRoom chatRoom = chatService.insertChatRoom(type, id);

        modelAndView.addObject("chatRoom", chatRoom);
        modelAndView.addObject("message", "Chat room created successfully");
        modelAndView.setViewName("chat/chatRoom/"+chatRoom.getId());

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
