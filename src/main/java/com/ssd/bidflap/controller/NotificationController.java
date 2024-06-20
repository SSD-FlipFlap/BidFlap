package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.ChatMessage;
import com.ssd.bidflap.domain.Notification;
import com.ssd.bidflap.domain.enums.NotificationType;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    @GetMapping("/notification/check")
    public ResponseEntity<Boolean> checkNotifications(HttpSession session) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return ResponseEntity.ok(false);
        }

        boolean hasUnreadNotifications = notificationService.hasUnreadNotifications(nickname);
        return ResponseEntity.ok(hasUnreadNotifications);
    }

    @GetMapping("notification/unread")
    public String getUnreadNotifications(HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            return "redirect:/auth/login";
        }

        Long memberId = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Invalid nickname: " + nickname))
                .getId();

        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(memberId);
        model.addAttribute("unreadNotifications", unreadNotifications);
        model.addAttribute("hasUnreadNotifications", !unreadNotifications.isEmpty());
        return "thyme/Notification";
    }

//    @PostMapping("/create")
//    public Notification createChatNotification(@Valid @RequestBody ChatMessage chatMessage) {
//        return notificationService.createChatNotification(chatMessage);
//    }

    @PutMapping("notification/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}