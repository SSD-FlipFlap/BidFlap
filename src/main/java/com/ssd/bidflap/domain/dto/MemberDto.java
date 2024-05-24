package com.ssd.bidflap.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter @Builder
    public static class ChangePasswordDto {
        @NotEmpty(message="현재 비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "현재 비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.")
        private String password;

        @NotEmpty(message="새 비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "새 비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.")
        private String newPassword;

        @NotEmpty(message="새 비밀번호 확인은 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "새 비밀번호 확인은 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.")
        private String confirmPassword;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter @Builder
    public static class UpdateMemberDto {
        @NotEmpty(message="이메일은 필수입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotEmpty(message="닉네임은 필수입니다.")
        private String nickname;

        private String expert;

        private String expertInfo;

        private String asPrice;

        @NotEmpty(message="은행 이름은 필수입니다.")
        private String bank;

        @NotEmpty(message="계좌 번호는 필수입니다.")
        private String accountNumber;

        private List<String> category = new ArrayList<>();

        @Nullable
        private String profile;
    }

    @Builder
    @Getter
    public static class SimpleInfoResponseDto {
        private String nickname;
        private String email;
        private String profile;
    }

}
