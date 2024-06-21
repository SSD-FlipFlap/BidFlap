package com.ssd.bidflap.service;

import com.ssd.bidflap.config.aws.AmazonS3Manager;
import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.Interest;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Uuid;
import com.ssd.bidflap.domain.dto.FindEmailDto;
import com.ssd.bidflap.domain.dto.LoginDto;
import com.ssd.bidflap.domain.dto.ResetPasswordDto;
import com.ssd.bidflap.domain.dto.SignUpDto;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.domain.enums.MemberRole;
import com.ssd.bidflap.exception.MemberException;
import com.ssd.bidflap.repository.AfterServiceRepository;
import com.ssd.bidflap.repository.InterestRepository;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final AfterServiceRepository afterServiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Transactional
    public void signUp(SignUpDto signUpDto) {

        // as 세부 정보 입력값 검증
        if (signUpDto.getExpert().equals("yes")) {
            validAsInput(signUpDto.getExpertInfo(), signUpDto.getAsPrice());
        }

        // 이메일 중복 검증
        validateEmail(signUpDto.getEmail());

        // 닉네임 중복 검증
        validateNickname(signUpDto.getNickname());

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(signUpDto.getPassword());

        // 이미지 업로드
        String profileUrl = null;
        if (signUpDto.getProfile() != null && !signUpDto.getProfile().isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());
            profileUrl = s3Manager.uploadFile(s3Manager.generateProfileKeyName(savedUuid), signUpDto.getProfile());
        }

        // 회원 정보 저장
        Member newMember = Member.builder()
                .email(signUpDto.getEmail())
                .password(encryptedPassword)
                .nickname(signUpDto.getNickname())
                .bank(signUpDto.getBank())
                .account(signUpDto.getAccountNumber())
                .profile(profileUrl)
                .depositBalance(300000)
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
        List<String> categories = signUpDto.getCategory();
        if (categories != null && !categories.isEmpty()) {
            for (String category : categories) {
                Interest interest = Interest.builder()
                        .member(savedMember)
                        .category(Category.valueOf(category.trim().toUpperCase()))
                        .build();
                interestRepository.save(interest);
            }
        }

    }

    protected void validateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberException.EmailDuplicatedException("이미 등록된 이메일입니다.");
        }
    }

    protected void validateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberException.NicknameDuplicatedException("이미 등록된 닉네임입니다.");
        }
    }

    protected void validAsInput(String asDescription, String asPrice) {
        if (asDescription == null || asDescription.trim().isEmpty()) {
            throw new MemberException.AsDescInputException("소개를 입력해주세요.");
        }
        if (asPrice == null || asPrice.trim().isEmpty()) {
            throw new MemberException.AsPriceInputException("가격을 입력해주세요");
        }
    }


    @Transactional(readOnly = true)
    public String login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보가 일치하지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }

        return member.getNickname();
    }

    @Transactional(readOnly = true)
    public String findEmail(FindEmailDto findEmailDto) {
        String nickname = findEmailDto.getNickname();
        String bank = findEmailDto.getBank();
        String account = findEmailDto.getAccountNumber();

        Member member =  memberRepository.findEmailByNicknameAndBankAndAccount(nickname, bank, account)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        return member.getEmail();
    }

    @Transactional
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        Member member = memberRepository.findByEmailAndNickname(resetPasswordDto.getEmail(), resetPasswordDto.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        String newPassword = resetPasswordDto.getNewPassword();
        String confirmPassword = resetPasswordDto.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalStateException("새 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(newPassword);
        member.changePassword(encryptedPassword);
        memberRepository.save(member);
    }
}
