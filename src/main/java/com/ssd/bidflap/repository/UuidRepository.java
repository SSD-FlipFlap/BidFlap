package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}