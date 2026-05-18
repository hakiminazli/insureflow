package com.insureflow.member.service;

import com.insureflow.member.dto.CreateMemberRequest;
import com.insureflow.member.dto.EligibilityResponse;
import com.insureflow.member.dto.MemberResponse;
import com.insureflow.member.entity.Member;
import com.insureflow.member.entity.MemberStatus;
import com.insureflow.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberRepository.existsByMemberNumber(request.getMemberNumber())) {
            throw new IllegalArgumentException("Member number already exists");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (request.getCoverageEndDate().isBefore(request.getCoverageStartDate())) {
            throw new IllegalArgumentException("Coverage end date cannot be before coverage start date");
        }

        Member member = Member.builder()
                .memberNumber(request.getMemberNumber())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .plan(request.getPlan())
                .status(request.getStatus() == null ? MemberStatus.ACTIVE : request.getStatus())
                .coverageStartDate(request.getCoverageStartDate())
                .coverageEndDate(request.getCoverageEndDate())
                .build();

        return toResponse(memberRepository.save(member));
    }

    public MemberResponse getMember(Long id) {
        return toResponse(findMember(id));
    }

    public EligibilityResponse checkEligibility(Long id) {
        Member member = findMember(id);
        LocalDate today = LocalDate.now();

        if (member.getStatus() != MemberStatus.ACTIVE) {
            return toEligibilityResponse(member, false, "Member status is " + member.getStatus());
        }

        if (today.isBefore(member.getCoverageStartDate())) {
            return toEligibilityResponse(member, false, "Coverage has not started");
        }

        if (today.isAfter(member.getCoverageEndDate())) {
            return toEligibilityResponse(member, false, "Coverage has expired");
        }

        return toEligibilityResponse(member, true, "Member is eligible");
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    private MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .memberNumber(member.getMemberNumber())
                .fullName(member.getFullName())
                .email(member.getEmail())
                .dateOfBirth(member.getDateOfBirth())
                .plan(member.getPlan())
                .status(member.getStatus())
                .coverageStartDate(member.getCoverageStartDate())
                .coverageEndDate(member.getCoverageEndDate())
                .build();
    }

    private EligibilityResponse toEligibilityResponse(Member member, boolean eligible, String reason) {
        return EligibilityResponse.builder()
                .memberId(member.getId())
                .memberNumber(member.getMemberNumber())
                .eligible(eligible)
                .plan(member.getPlan())
                .status(member.getStatus())
                .reason(reason)
                .build();
    }
}
