package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.ProductStatus;
import com.ssd.bidflap.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_generator")
    @SequenceGenerator(name = "product_seq_generator", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    private Long id;

    private String title;

    @Lob
    private String description;

    @ColumnDefault("0")
    private Integer likeCount;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.SELLING;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Integer finalBidPrice;

    // 경매 정보
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductLike> productLikeList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> productImageList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRoomList;
}
