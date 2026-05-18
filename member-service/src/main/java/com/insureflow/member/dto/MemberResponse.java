package com.insureflow.member.dto;

import com.insureflow.member.entity.MemberStatus;
import com.insureflow.member.entity.PlanType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MemberResponse {
    private Long id;
    private String memberNumber;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private PlanType plan;
    private MemberStatus status;
    private LocalDate coverageStartDate;
    private LocalDate coverageEndDate;
}
