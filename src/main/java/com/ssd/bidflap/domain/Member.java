package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String bank;

    private String account; // 계좌 번호

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private String profile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Interest> interests = new ArrayList<>();

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    public void updateMember(String email, String nickname, String bank, String account) {
        this.email = email;
        this.nickname = nickname;
        this.bank = bank;
        this.account = account;
    }

}
