package com.ssd.bidflap.service;

import com.ssd.bidflap.domain.AfterService;
import com.ssd.bidflap.repository.AfterServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsService {
    private final AfterServiceRepository afterServiceRepository;

    public List<AfterService> findAllAfterServiceList(){
        return afterServiceRepository.findAll();
    }

    public Optional<AfterService> findOptionalAfterService(long afterServiceId){
        return afterServiceRepository.findById(afterServiceId);
    }
}
