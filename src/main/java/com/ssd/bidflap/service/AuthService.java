package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.Interest;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.dto.SignUpDto;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.domain.enums.MemberRole;
import com.ssd.bidflap.repository.AfterServiceRepository;
import com.ssd.bidflap.repository.InterestRepository;
import com.ssd.bidflap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final AfterServiceRepository afterServiceRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerMember(SignUpDto signUpDto) {

        // 이메일 중복 검증
        validateEmail(signUpDto.getEmail());

        // 닉네임 중복 검증
        validateNickname(signUpDto.getNickname());

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(signUpDto.getPassword());

        // 회원 정보 저장
        Member newMember = Member.builder()
                .email(signUpDto.getEmail())
                .password(encryptedPassword)
                .nickname(signUpDto.getNickname())
                .bank(signUpDto.getBank())
                .account(signUpDto.getAccountNumber())
                .profile("")    // TODO 프로필 저장 로직 구현
                .memberRole(MemberRole.USER)
                .build();

        Member savedMember = memberRepository.save(newMember);

        // as 전문가 등록
        if (signUpDto.getExpert().equalsIgnoreCase("yes")) {
            // as 객체 생성
            String price = signUpDto.getAsPrice();
            String description = signUpDto.getExpertInfo();

            if (price != null && description != null) {
                AfterService afterService = AfterService.builder()
                        .member(savedMember)
                        .price(Integer.valueOf(price))
                        .description(description)
                        .build();

                afterServiceRepository.save(afterService);
            }
        }

        // 관심 분야 저장
        String[] categories = signUpDto.getCategory();
        if (categories != null && categories.length > 0) {
            for (String category : categories) {
                Interest interest = Interest.builder()
                        .member(savedMember)
                        .category(Category.valueOf(category.trim().toUpperCase()))
                        .build();
                interestRepository.save(interest);
            }
        }

    }

    private void validateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("이미 등록된 이메일입니다.");
        }
    }

    private void validateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalStateException("이미 등록된 닉네임입니다.");
        }
    }

}
