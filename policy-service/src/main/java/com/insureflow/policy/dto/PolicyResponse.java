package com.insureflow.policy.dto;

import com.insureflow.policy.entity.PlanType;
import com.insureflow.policy.entity.PolicyStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PolicyResponse {
    private Long id;
    private String policyNumber;
    private PlanType plan;
    private PolicyStatus status;
    private List<PolicyBenefitResponse> benefits;
}
