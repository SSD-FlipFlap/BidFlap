package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.AuctionStatus;
import com.ssd.bidflap.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

//    private String product_filepath;
    
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 경매 정보
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
