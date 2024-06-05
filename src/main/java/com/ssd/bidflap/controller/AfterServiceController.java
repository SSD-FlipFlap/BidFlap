package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.ChatRoom;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.service.AsService;
import com.ssd.bidflap.service.ChatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/afterService")
public class AfterServiceController {
    private final AsService asService;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    //목록 띄우기
    @GetMapping("/asList")
    public ModelAndView getAfterServiceList(){
        ModelAndView mv = new ModelAndView();

        List<AfterService> asList = asService.findAllAfterServiceList();
        if(asList.isEmpty()) {
            asList = new ArrayList<>();
        }

        mv.addObject("asList", asList);
        mv.setViewName("thyme/afterService/asList");

        return mv;
    }
    //상세조회
    @GetMapping("/asInfo/{afterServiceId}")
    public ModelAndView getAfterServiceInfo(@PathVariable long afterServiceId, HttpSession session){
        ModelAndView mv = new ModelAndView();
        String nickname = (String) session.getAttribute("loggedInMember");

        Optional<AfterService> optionalAfterService = asService.findOptionalAfterService(afterServiceId);
        if(optionalAfterService.isEmpty()){
            throw new NotFoundException("afterService info를 찾지 못 했습니다.");
        }

        mv.addObject("asInfo", optionalAfterService.get());

        List<ChatRoom> chatRoomList = new ArrayList<>();
        try{
            if(!memberRepository.findByNickname(nickname).isEmpty() && optionalAfterService.get().getMember().getNickname().equals(nickname))
                chatRoomList = chatService.findByAfterServiceId(afterServiceId);
            else if(!memberRepository.findByNickname(nickname).isEmpty())
                chatRoomList = chatService.findByAfterServiceIdAndNickname(afterServiceId, nickname);
            mv.addObject("sizeOfList", chatRoomList.size());
        }catch(Exception e){
            mv.addObject("sizeOfList", "생성된 채팅방이 없습니다.");
        }
        mv.addObject("chatRoomList", chatRoomList);
        mv.setViewName("thyme/afterService/asInfo");

        return mv;
    }
    //검색
    @GetMapping("/search")
    public ModelAndView getsearchedAfterServiceList(@RequestParam String keyword){
        ModelAndView mv = new ModelAndView();

        List<AfterService> asList = asService.findByDescriptionContaining(keyword);
        if(asList.isEmpty()) {
            asList = new ArrayList<>();
        }

        mv.addObject("asList", asList);
        mv.setViewName("thyme/afterService/asList");
        return mv;
    }
}
