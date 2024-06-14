package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @SequenceGenerator(name = "member_seq_generator", sequenceName = "MEMBER_SEQ", allocationSize = 1)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String bank;

    private String account; // 계좌 번호

    private Integer depositBalance; //보증금잔고
    
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole = MemberRole.USER;

    private String profile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Interest> interestList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Product> productList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ProductLike> productLikeList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Purchase> purchaseList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bidder> bidderList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<AfterService> afterServiceList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRoomList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessageList;

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    public void updateMember(String email, String nickname, String bank, String account) {
        this.email = email;
        this.nickname = nickname;
        this.bank = bank;
        this.account = account;
    }

    public void changeProfile(String profile) {
        this.profile = profile;
    }

    public void setDepositBalance(Integer depositBalance) {
        this.depositBalance = depositBalance;
    }
}
