package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.service.AsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/afterService")
public class AfterServiceController {
    private final AsService asService;
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
    public ModelAndView getAfterServiceInfo(@PathVariable long afterServiceId){
        ModelAndView mv = new ModelAndView();
        Optional<AfterService> optionalAfterService = asService.findOptionalAfterService(afterServiceId);
        //if(optionalAfterService.isEmpty())

        mv.addObject("asInfo", optionalAfterService.get());
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
