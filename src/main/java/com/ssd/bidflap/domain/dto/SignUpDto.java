package com.ssd.bidflap.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class SignUpDto {

    @NotEmpty(message="이메일은 필수입니다.")
    private String email;

    @NotEmpty(message="비밀번호는 필수입니다.")
    private String password;

    @NotEmpty(message="닉네임은 필수입니다.")
    private String nickname;

    @NotEmpty
    private String expert;

    @Nullable
    private String expertInfo = null;

    @Nullable
    private String asPrice = null;

    @NotEmpty(message="은행 이름은 필수입니다.")
    private String bank;

    @NotEmpty(message="계좌 번호는 필수입니다.")
    private String accountNumber;

    @Nullable
    private MultipartFile profile;

    @Nullable
    private String[] category;
}
