package com.insureflow.member.repository;

import com.insureflow.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberNumber(String memberNumber);

    boolean existsByEmail(String email);

    Optional<Member> findByMemberNumber(String memberNumber);
}
