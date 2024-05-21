package com.ssd.bidflap.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {

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

    public static class UpdateMemberDto {
        private String email;
        private String nickname;
        private String expert = "no";
        private String expertInfo;
        private String asPrice;
        private String bank;
        private String accountNumber;
        private String[] category;
    }

    public static class ChangeProfileDto {
        private MultipartFile profile;
    }
}
