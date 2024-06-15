package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Member;
import com.ssd.bidflap.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByMember(Member member);

    @Modifying
    @Query("UPDATE Purchase p SET p.member = :unknownMember WHERE p.member = :member")
    void updateMemberToUnknown(@Param("member") Member member, @Param("unknownMember") Member unknownMember);
}
