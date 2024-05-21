package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.dto.UserDto;
import com.ssd.bidflap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(String nickname, UserDto.ChangePasswordDto changePasswordDto) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        String originalPassword = changePasswordDto.getPassword();
        if (!passwordEncoder.matches(originalPassword, member.get().getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String newPassword = changePasswordDto.getNewPassword();
        String confirmPassword = changePasswordDto.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalStateException("새 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(newPassword);
        member.get().changePassword(encryptedPassword);
        memberRepository.save(member.get());
    }

}
