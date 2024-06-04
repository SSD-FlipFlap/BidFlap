package com.ssd.bidflap.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Auction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq_generator")
    @SequenceGenerator(name = "auction_seq_generator", sequenceName = "AUCTION_SEQ", allocationSize = 1)
    private Long id;

    @ColumnDefault("1")
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

}
