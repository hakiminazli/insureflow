package com.insureflow.member.dto;

import com.insureflow.member.entity.MemberStatus;
import com.insureflow.member.entity.PlanType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibilityResponse {
    private Long memberId;
    private String memberNumber;
    private boolean eligible;
    private PlanType plan;
    private MemberStatus status;
    private String reason;
}
