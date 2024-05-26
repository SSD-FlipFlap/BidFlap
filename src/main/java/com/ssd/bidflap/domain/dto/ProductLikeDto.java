package com.ssd.bidflap.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductLikeDto {
    private Long memberId;
    private Long productId;

    public ProductLikeDto(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
    }
}
