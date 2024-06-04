package com.ssd.bidflap.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindEmailDto {

    @NotEmpty(message="닉네임은 필수입니다.")
    private String nickname;

    @NotEmpty(message="은행 이름은 필수입니다.")
    private String bank;

    @NotEmpty(message="계좌 번호는 필수입니다.")
    private String accountNumber;
}
