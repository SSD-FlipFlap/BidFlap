package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.domain.dto.ChatMessageDto;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ChatService;
import com.ssd.bidflap.service.MemberService;
import com.ssd.bidflap.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

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
        //product, seller, sender, chatRoom, chatRoomMessages
        String nickname = (String) session.getAttribute("loggedInMember");

        //product
        Product product = productRepository.findById(productId);
        modelAndView.addObject("product", product);
        System.out.println(product.getTitle());
        /*
        //seller
        Optional<Member> optionalSeller = memberRepository.findByNickname(product.getMember().getNickname());
        if(optionalSeller.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member seller = optionalSeller.get();
        modelAndView.addObject("seller", seller);
        */

        //sender
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member sender = optionalMember.get();
        modelAndView.addObject("sender", sender);
        //chatRoom
        Optional<ChatRoom> optionalChatRoom = chatService.getChatRoomByProductIdAndNickname(productId, nickname);
        if(optionalChatRoom.isEmpty()){
            return createChatRoom(product, sender,"product", productId);    //rollback 필요
        }
        modelAndView.addObject("chatRoom", optionalChatRoom.get());
        //chatMessages
        List<ChatMessage> chatMessages = chatService.getChatMessagesByChatRoomId(optionalChatRoom.get().getId());
        modelAndView.addObject("chatMessages", chatMessages);

        modelAndView.setViewName("chat/chatRoom");
        //modelAndView.setViewName("thyme/chat/chatRoom");

        return modelAndView;
    }
    /*
    //입장 - afterService
    @GetMapping("/chatRoom/afterService/{afterServiceId}")
    public ModelAndView getChatRoomByAfterServiceId(@PathVariable long afterServiceId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        //afterService, asSeller, sender, chatRoom, chatRoomMessages
        String nickname = (String) session.getAttribute("loggedInMember");
        //afterService
        AfterService afterService = asRepository.findById(afterServiceId);
        modelAndView.addObject("afterService", afterService);
        //////////////////////////////////////////////////
        //asSeller
        Optional<Member> optionalSeller = memberRepository.findByNickname(product.getMember().getNickname());
        if(optionalSeller.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member seller = optionalSeller.get();
        modelAndView.addObject("seller", seller);
        /////////////////////////////////////////////////

        //sender
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member sender = optionalMember.get();
        modelAndView.addObject("sender", sender);
        //chatRoom
        Optional<ChatRoom> optionalChatRoom = chatService.getChatRoomByAfterServiceIdAndNickname(afterServiceId, nickname);
        if(optionalChatRoom.isEmpty())
            return createChatRoom(afterService, sender,"afterService", afterServiceId);    //생성
        modelAndView.addObject("chatRoom", optionalChatRoom.get());

        //chatRoomMessages
        List<ChatMessage> chatMessages=chatService.getChatMessagesByChatRoomId(optionalChatRoom.get().getId());
        modelAndView.addObject("chatMessages", chatMessages);

        modelAndView.setViewName("chat/chatRoom");

        //modelAndView.setViewName("thyme/chat/chatRoom");

        return modelAndView;
    }
    */
    //생성
    @PostMapping("/createChatRoom")
    private ModelAndView createChatRoom(Product product, Member sender, String type, long id) {
        ModelAndView modelAndView = new ModelAndView();
        ChatRoom chatRoom = chatService.insertChatRoom(type, id);

        modelAndView.addObject("chatRoom", chatRoom);
        modelAndView.addObject("product", product);
        modelAndView.addObject("sender", sender);
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
