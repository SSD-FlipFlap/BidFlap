package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.AuctionCancelStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Bidder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bidder_seq_generator")
    @SequenceGenerator(name = "bidder_seq_generator", sequenceName = "BIDDER_SEQ", allocationSize = 1)
    private Long id;

    private Integer price;

    private Integer deposit;

    @Enumerated(EnumType.STRING)
    private AuctionCancelStatus cancelStatus = AuctionCancelStatus.NOT_CANCEL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
