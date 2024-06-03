package com.ssd.bidflap.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Auction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer period;


    private Integer highPrice;

    private Long successfulBidder;    // 낙찰자(Bidder의 pk)

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bidder> bidderList = new ArrayList<>();


    @Column(name = "product_id")
    private Long productId;

    public void updateHighPrice(Integer highPrice) {
        this.highPrice = highPrice;
    }

//    public LocalDateTime getEndTime() {
//        return getCreatedDate().plusDays(period);
//    }

//    public Duration getRemainingTime() {
//        return Duration.between(LocalDateTime.now(), getEndTime());
//    }

}
