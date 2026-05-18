package com.insureflow.member.controller;

import com.insureflow.member.dto.EligibilityResponse;
import com.insureflow.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/members")
@RequiredArgsConstructor
public class InternalMemberController {

    private final MemberService memberService;

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<EligibilityResponse> checkEligibility(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.checkEligibility(id));
    }
}
