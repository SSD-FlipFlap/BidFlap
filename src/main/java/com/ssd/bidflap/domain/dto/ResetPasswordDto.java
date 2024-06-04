package com.ssd.bidflap.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetPasswordDto {

    private String email;

    private String nickname;

    private String newPassword;

    private String confirmPassword;
}
