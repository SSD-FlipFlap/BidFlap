package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.dto.*;
import com.ssd.bidflap.domain.enums.MemberRole;
import com.ssd.bidflap.exception.MemberException;
import com.ssd.bidflap.interceptor.Auth;
import com.ssd.bidflap.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    // 뷰에 은행 목록을 전달(drop-down list)
    @ModelAttribute("bankList")
    public List<String> bankList() {
        List<String> bankList = new ArrayList<>();
        bankList.add("국민");
        bankList.add("신한");
        bankList.add("우리");
        bankList.add("하나");
        bankList.add("농협");
        bankList.add("수협");
        bankList.add("산업");
        bankList.add("카카오뱅크");
        return bankList;
    }

    // 로그아웃
    @Auth(role = MemberRole.USER)
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutSuccess", "true");
        return "redirect:/";
    }

    // 프로필 관리
    @Auth(role = MemberRole.USER)
    @GetMapping("/edit-profile")
    public String editProfile(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");

        // 기존 회원 정보 가져오기
        MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);

        model.addAttribute("changePasswordDto", new MemberDto.ChangePasswordDto());
        model.addAttribute("updateMemberDto", memberInfo);
        return "thyme/member/editProfile";
    }

    // 비밀번호 변경
    @Auth(role = MemberRole.USER)
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") MemberDto.ChangePasswordDto changePasswordDto,
                                 BindingResult result, HttpSession session, // bindingResilt는 @ModelAttribute 다음에 위치해야 한다.
                                 Model model, RedirectAttributes redirectAttributes) {
        // 로그인한 회원의 닉네임(세션에 저장된 값)
        String nickname = (String) session.getAttribute("loggedInMember");

        if (result.hasErrors()) {
            MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);
            model.addAttribute("updateMemberDto", memberInfo);
            model.addAttribute("changePasswordDto", changePasswordDto);
            return "thyme/member/editProfile";
        }

        try {
            memberService.changePassword(nickname, changePasswordDto);
            redirectAttributes.addFlashAttribute("updateSuccess", true);
            return "redirect:/members/edit-profile";
        } catch (IllegalArgumentException e) {
            MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);
            model.addAttribute("updateMemberDto", memberInfo);
            model.addAttribute("passwordError", e.getMessage());
            return "thyme/member/editProfile";
        } catch (IllegalStateException e) {
            MemberDto.UpdateMemberDto memberInfo = memberService.getMemberInfoByNickname(nickname);
            model.addAttribute("updateMemberDto", memberInfo);
            model.addAttribute("newPasswordError", e.getMessage());
            return "thyme/member/editProfile";
        }
    }

    // 회원 정보 수정
    @Auth(role = MemberRole.USER)
    @PostMapping("/update-member")
    public String updateMember(@Valid @ModelAttribute("updateMemberDto") MemberDto.UpdateMemberDto updateMemberDto,
                                 BindingResult result, HttpSession session,
                                 Model model, RedirectAttributes redirectAttributes) {
        String nickname = (String) session.getAttribute("loggedInMember");

        if (result.hasErrors()) {
            model.addAttribute("changePasswordDto", new MemberDto.ChangePasswordDto());
            model.addAttribute("updateMemberDto", updateMemberDto);
            return "thyme/member/editProfile";
        }

        model.addAttribute("changePasswordDto", new MemberDto.ChangePasswordDto());

        try {
            // 세션의 닉네임 갱신
            String newNickname = memberService.updateMember(nickname, updateMemberDto);
            session.setAttribute("loggedInMember", newNickname);

            redirectAttributes.addFlashAttribute("updateMemberSuccess", true);
            return "redirect:/members/edit-profile";
        } catch (MemberException.EmailDuplicatedException e) {
            model.addAttribute("emailError", e.getMessage());
        } catch (MemberException.NicknameDuplicatedException e) {
            model.addAttribute("nicknameError", e.getMessage());
        } catch (MemberException.AsDescInputException e) {
            model.addAttribute("asInfoError", e.getMessage());
        } catch (MemberException.AsPriceInputException e) {
            model.addAttribute("asPriceError", e.getMessage());
        }
        return "thyme/member/editProfile";
    }

    // 프로필 사진 변경
    @Auth(role = MemberRole.USER)
    @PostMapping("/change-profile")
    public String updateMember(@RequestParam MultipartFile profile,HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");

        try {
            memberService.changeProfile(nickname, profile);
        } catch (Exception e) {
            model.addAttribute("error", "프로필 변경 중 오류가 발생했습니다.");
            return "thyme/member/editProfile";
        }

        return "redirect:/members/edit-profile";
    }
    
    // 회원 탈퇴
    @Auth(role = MemberRole.USER)
    @PostMapping("/delete")
    public String deleteMember(HttpSession session, RedirectAttributes redirectAttributes) {
        String nickname = (String) session.getAttribute("loggedInMember");

        boolean isSuccess = memberService.deleteMember(nickname);
        redirectAttributes.addFlashAttribute("deleteSuccess", isSuccess);
        session.invalidate();
        return "redirect:/";
    }
}