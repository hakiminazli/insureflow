package com.insureflow.policy.dto;

import com.insureflow.policy.entity.BenefitCategory;
import com.insureflow.policy.entity.PlanType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PolicyValidationResponse {
    private boolean covered;
    private PlanType plan;
    private BenefitCategory category;
    private BigDecimal requestedAmount;
    private BigDecimal coverageLimit;
    private BigDecimal copayAmount;
    private String reason;
}
