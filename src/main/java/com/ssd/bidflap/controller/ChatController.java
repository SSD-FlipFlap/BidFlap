package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.*;
import com.ssd.bidflap.domain.dto.ChatMessageDto;
import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.repository.AfterServiceRepository;
import com.ssd.bidflap.repository.ChatRoomRepository;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ChatService;
import com.ssd.bidflap.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final AfterServiceRepository asRepository;
    private final ProductService productService;

    //입장 - product
    @GetMapping("/chatRoom/product/{roomId}")
    public ModelAndView getChatRoomById(@PathVariable long roomId,  HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("chat/chatRoom");
        modelAndView.addObject("crType", "product");
        //product, seller, sender, chatRoom, chatRoomMessages
        String nickname = (String) session.getAttribute("loggedInMember");

        //chatRoom
        Optional<ChatRoom> optionalChatRoom = chatService.getChatRoomById(roomId);
        if(optionalChatRoom.isEmpty()){
            throw new NotFoundException("채팅방이 없습니다.");
        }
        modelAndView.addObject("chatRoom", optionalChatRoom.get());

        //product
        Optional<Product> optionalProduct = productRepository.findById(optionalChatRoom.get().getProduct().getId());
        if(optionalProduct.isEmpty()){
            modelAndView.addObject("message", "사용할 수 없는 채팅방입니다.");
            return modelAndView;
        }
        modelAndView.addObject("product", optionalProduct.get());

        //seller
        Optional<Member> optionalSeller = memberRepository.findByNickname(optionalProduct.get().getMember().getNickname());
        if(optionalSeller.isEmpty()){
            throw new UsernameNotFoundException("판매자를 찾을 수 없습니다.");
        }
        modelAndView.addObject("seller", optionalSeller.get());

        if(!nickname.equals(optionalSeller.get().getNickname()) && !nickname.equals(optionalChatRoom.get().getMember().getNickname()))
            throw new AccessDeniedException("접근 가능한 사용자가 아닙니다.");

        //sender
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member sender = optionalMember.get();
        modelAndView.addObject("sender", sender);

        //chatMessages
        List<ChatMessage> chatMessages = chatService.getChatMessagesByChatRoomId(optionalChatRoom.get().getId());
        modelAndView.addObject("chatMessages", chatMessages);

        // 거래 내역
        int soldCounts = productService.countProductsByMemberAndStatus(nickname, ProductStatus.SOLD);
        modelAndView.addObject("soldCounts", soldCounts);

        modelAndView.addObject("message", "채팅가능");

        return modelAndView;
    }

    //입장 - afterService
    @GetMapping("/chatRoom/afterService/{roomId}")
    public ModelAndView getChatRoomByAfterServiceId(@PathVariable long roomId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("chat/chatRoom");
        modelAndView.addObject("crType", "afterService");

        //afterService, seller, sender, chatRoom, chatRoomMessages
        String nickname = (String) session.getAttribute("loggedInMember");
        //chatRoom
        Optional<ChatRoom> optionalChatRoom = chatService.getChatRoomById(roomId);
        if(optionalChatRoom.isEmpty())
            throw new NotFoundException("채팅방이 없습니다.");
        modelAndView.addObject("chatRoom", optionalChatRoom.get());

        //afterService
        Optional<AfterService> optionalAfterService = asRepository.findById(optionalChatRoom.get().getAfterService().getId());
        if(optionalAfterService.isEmpty()){
            //throw new NotFoundException("afterService가 없습니다.");
            modelAndView.addObject("message", "사용할 수 없는 채팅방입니다.");
            return modelAndView;
        }
        modelAndView.addObject("product", optionalAfterService.get());
        //seller
        Optional<Member> optionalSeller = memberRepository.findByNickname(optionalAfterService.get().getMember().getNickname());
        if(optionalSeller.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member seller = optionalSeller.get();
        modelAndView.addObject("seller", seller);

        if(!nickname.equals(optionalSeller.get().getNickname()) && !nickname.equals(optionalChatRoom.get().getMember().getNickname()))
            throw new AccessDeniedException("접근 가능한 사용자가 아닙니다.");

        //sender
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member sender = optionalMember.get();
        modelAndView.addObject("sender", sender);

        //chatRoomMessages
        List<ChatMessage> chatMessages=chatService.getChatMessagesByChatRoomId(optionalChatRoom.get().getId());
        modelAndView.addObject("chatMessages", chatMessages);

        modelAndView.addObject("message", "채팅가능");

        return modelAndView;
    }

    //생성
    @PostMapping("/createChatRoom/{pId}")
    private RedirectView createChatRoom(@PathVariable long pId, HttpSession session) {
        /*
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("chat/chatRoom");
        modelAndView.addObject("crType", "product");
        */
        String nickname = (String) session.getAttribute("loggedInMember");
        //product
        Optional<Product> optionalProduct = productRepository.findById(pId);
        if(optionalProduct.isEmpty())
            throw new NotFoundException("product를 찾을 수 없습니다.");
        //modelAndView.addObject("product", optionalProduct.get());

        //sender
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        //modelAndView.addObject("sender", optionalMember.get());

        ChatRoom chatRoom = chatService.insertChatRoom("product", optionalProduct.get().getId(), optionalMember.get());

        long roomId = chatRoom.getId();
        RedirectView redirectView = new RedirectView("/chat/chatRoom/product/" + roomId);
        return redirectView;

        /*
        List<ChatMessage> chatRoomMessages = new ArrayList<>();
        modelAndView.addObject("chatRoom", chatRoom);

        //seller
        Optional<Member> optionalSeller = memberRepository.findByNickname(optionalProduct.get().getMember().getNickname());
        if(optionalSeller.isEmpty()){
            throw new UsernameNotFoundException("판매자를 찾을 수 없습니다.");
        }
        modelAndView.addObject("seller", optionalSeller.get());

        modelAndView.addObject("chatRoomMessages", chatRoomMessages);

        modelAndView.addObject("message", "채팅가능");
        return modelAndView;
        */

    }

    @PostMapping("/createASChatRoom/{afterServiceId}")
    private RedirectView createASChatRoom(@PathVariable long afterServiceId, HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");

        //afterService
        Optional<AfterService> optionalAfterService = asRepository.findById(afterServiceId);
        if(optionalAfterService.isEmpty()){
            throw new NotFoundException("afterService가 없습니다.");
        }

        //sender
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        ChatRoom chatRoom = chatService.insertChatRoom("afterService", optionalAfterService.get().getId(), optionalMember.get());

        long roomId = chatRoom.getId();
        RedirectView redirectView = new RedirectView("/chat/chatRoom/afterService/" + roomId);
        return redirectView;
    }

    //채팅방 삭제
    @DeleteMapping("/deleteChatRoom/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.ok("Chat room deleted successfully");
    }

    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야 함
    public ChatMessageDto sendMessage(@DestinationVariable Long roomId, ChatMessageDto message) {
        ChatMessage mm = chatService.createMessage(roomId, message.getMember(), message.getMessage());
        return ChatMessageDto.builder()
                .roomId(roomId)
                .member(message.getMember())
                .message(message.getMessage())
                .build();
    }
    /*
    @DeleteMapping("/delete/{chatRoomId}")
    public ModelAndView deleteMessage(@PathVariable int chatRoomId) {
        ModelAndView modelAndView = new ModelAndView();

        chatService.deleteMessages(chatRoomId);
        modelAndView.addObject("message", "Messages deleted successfully");
        modelAndView.setViewName("redirect:/chatRoom/" + chatRoomId);
        return modelAndView;
    }
    */
}
