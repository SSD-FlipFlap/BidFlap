package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String bank;

    private String account; // 계좌 번호

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profile;
}
