package com.ssd.bidflap.repository;

import com.ssd.bidflap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findEmailByNicknameAndBankAndAccount(String nickname, String bank, String account);
}
