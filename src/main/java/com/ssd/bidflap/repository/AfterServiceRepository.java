package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AfterServiceRepository extends JpaRepository<AfterService, Long> {
    Optional<AfterService> findByMember(Member savedMember);
}
