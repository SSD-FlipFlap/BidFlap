package com.ssd.bidflap.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Purchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_seq_generator")
    @SequenceGenerator(name = "purchase_seq_generator", sequenceName = "PURCHASE_SEQ", allocationSize = 1)
    private Long id;

    private String name;

    private String zipcode;

    private String address;

    private String addressSpec;

    private String phone;

    @Nullable
    private String deliveryMessage;

    private String paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
