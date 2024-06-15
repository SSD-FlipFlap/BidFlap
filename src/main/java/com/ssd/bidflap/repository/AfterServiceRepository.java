package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface AfterServiceRepository extends JpaRepository<AfterService, Long> {
    Optional<AfterService> findByMember(Member savedMember);

    List<AfterService> findByDescriptionContaining(String keyword);

    @Modifying
    @Query("UPDATE AfterService a SET a.member = :unknownMember WHERE a.member = :member")
    void updateMemberToUnknown(@Param("member") Member member, @Param("unknownMember") Member unknownMember);
}
