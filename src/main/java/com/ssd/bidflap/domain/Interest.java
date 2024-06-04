package com.ssd.bidflap.domain;

import com.ssd.bidflap.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interest_seq_generator")
    @SequenceGenerator(name = "interest_seq_generator", sequenceName = "INTEREST_SEQ", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
