package com.ssd.bidflap.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetPasswordDto {

    @NotEmpty(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    private String email;

    @NotEmpty(message="닉네임은 필수입니다.")
    private String nickname;

    @NotEmpty(message="새 비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "새 비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.")
    private String newPassword;

    @NotEmpty(message="새 비밀번호 확인은 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "새 비밀번호 확인은 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.")
    private String confirmPassword;
}
