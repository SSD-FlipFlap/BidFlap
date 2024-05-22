package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Interest;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    void deleteByMember(Member member);
}
