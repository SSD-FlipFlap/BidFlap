package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface AfterServiceRepository extends JpaRepository<AfterService, Long> {
    Optional<AfterService> findByMember(Member savedMember);

    //List<AfterService> findAll();

    //AfterService save(AfterService afterService);

    //Optional<AfterService> findById(long afterServiceId);

    List<AfterService> findByDescriptionContaining(String keyword);
}
