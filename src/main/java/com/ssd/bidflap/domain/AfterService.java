package com.ssd.bidflap.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AfterService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "afterService", cascade = CascadeType.ALL)
    private List<Purchase> purchaseList;

    @OneToMany(mappedBy = "afterService", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRoomList;

    public void updateAfterService(String description, String price) {
        this.description = description;
        this.price = Integer.valueOf(price);
    }
}
