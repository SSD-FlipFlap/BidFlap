package com.ssd.bidflap.service;

import com.ssd.bidflap.config.aws.AmazonS3Manager;
import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.Interest;
import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Uuid;
import com.ssd.bidflap.domain.dto.MemberDto;
import com.ssd.bidflap.domain.enums.Category;
import com.ssd.bidflap.repository.AfterServiceRepository;
import com.ssd.bidflap.repository.InterestRepository;
import com.ssd.bidflap.repository.MemberRepository;
import com.ssd.bidflap.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AfterServiceRepository afterServiceRepository;
    private final InterestRepository interestRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Transactional
    public void changePassword(String nickname, MemberDto.ChangePasswordDto changePasswordDto) {
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

    @Transactional
    public String updateMember(String nickname, MemberDto.UpdateMemberDto updateMemberDto) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Member member = optionalMember.get();

        // as 세부 정보 입력값 검증
        if (updateMemberDto.getExpert().equals("yes")) {
            authService.validAsInput(updateMemberDto.getExpertInfo(), updateMemberDto.getAsPrice());
        }

        // 이메일 중복 검사
        if (!updateMemberDto.getEmail().equals(member.getEmail())) {
            authService.validateEmail(updateMemberDto.getEmail());
        }
        // 닉네임 중복 검사
        if (!updateMemberDto.getNickname().equals(member.getNickname())) {
            authService.validateNickname(updateMemberDto.getNickname());
        }

        // 회원 정보 저장
        member.updateMember(updateMemberDto.getEmail(), updateMemberDto.getNickname(), updateMemberDto.getBank(), updateMemberDto.getAccountNumber());
        Member updatedMember = memberRepository.save(member);

        // as 전문가 정보 수정
        Optional<AfterService> optionalAfterService = afterServiceRepository.findByMember(updatedMember);

        if (optionalAfterService.isPresent()) {     // 정보 존재 o
            AfterService afterService = optionalAfterService.get();
            if (updateMemberDto.getExpert().equalsIgnoreCase("yes")) {
                // 갱신(자기 소개, 가격)
                afterService.updateAfterService(updateMemberDto.getExpertInfo(), updateMemberDto.getAsPrice());
                afterServiceRepository.save(afterService);
            } else {
                // 삭제
                afterServiceRepository.delete(optionalAfterService.get());
            }
        } else {    // 정보 존재x
            if (updateMemberDto.getExpert().equalsIgnoreCase("yes")) {
                // 생성
                AfterService afterService = AfterService.builder()
                        .member(updatedMember)
                        .description(updateMemberDto.getExpertInfo())
                        .price(Integer.valueOf(updateMemberDto.getAsPrice()))
                        .build();
                afterServiceRepository.save(afterService);
            }
        }

        // 관심 분야 삭제 후 새로 저장
        interestRepository.deleteByMember(updatedMember);

        List<String> categories = updateMemberDto.getCategory();
        if (categories != null && !categories.isEmpty()) {
            for (String category : categories) {
                Interest interest = Interest.builder()
                        .member(updatedMember)
                        .category(Category.valueOf(category.trim().toUpperCase()))
                        .build();
                interestRepository.save(interest);
            }
        }

        return updatedMember.getNickname();
    }

    @Transactional
    public void changeProfile(String nickname, MultipartFile profile) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Member member = optionalMember.get();

        String newProfileUrl = null;
        String oldProfileUrl = member.getProfile();

        // 프로필 사진이 존재하지 않는데 삭제하는 경우
        if (oldProfileUrl == null && (profile == null || profile.isEmpty())) {
            return;
        }

        // 사용자가 파일을 선택하지 않은 경우(프로필 사진 삭제)
        if (profile == null || profile.isEmpty()) {
            s3Manager.deleteFile(oldProfileUrl);
            member.changeProfile(newProfileUrl);
            memberRepository.save(member);
            return;
        }

        // 프로필 사진이 존재하는 경우 기존 사진 삭제
        if (oldProfileUrl != null){
            s3Manager.deleteFile(oldProfileUrl);
        }

        // 저장
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        newProfileUrl = s3Manager.uploadFile(s3Manager.generateProfileKeyName(savedUuid), profile);

        member.changeProfile(newProfileUrl);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public MemberDto.UpdateMemberDto getMemberInfoByNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();

        String expert = "no";
        String expertInfo = "";
        String asPrice = "";

        Optional<AfterService> asInfo = afterServiceRepository.findByMember(member);
        if (asInfo.isPresent()) {
            expert = "yes";
            expertInfo = asInfo.get().getDescription();
            asPrice = String.valueOf(asInfo.get().getPrice());
        }

        // interest 객체 리스트를 String 리스트로 변환
        List<String> categories = member.getInterests().stream()
                .map(interest -> interest.getCategory().name().toLowerCase())
                .collect(Collectors.toList());

        MemberDto.UpdateMemberDto memberDto = MemberDto.UpdateMemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .expert(expert)
                .expertInfo(expertInfo)
                .asPrice(asPrice)
                .bank(member.getBank())
                .accountNumber(member.getAccount())
                .category(categories)
                .profile(member.getProfile())
                .build();

        return memberDto;
    }

    @Transactional(readOnly = true)
    public MemberDto.SimpleInfoResponseDto getSimpleInfoByNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();

        MemberDto.SimpleInfoResponseDto memberDto = MemberDto.SimpleInfoResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profile(member.getProfile())
                .build();

        return memberDto;
    }
}