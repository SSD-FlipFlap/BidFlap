package com.ssd.bidflap.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_like_seq_generator")
    @SequenceGenerator(name = "product_like_seq_generator", sequenceName = "PRODUCT_LIKE_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
