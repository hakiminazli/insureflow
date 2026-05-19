package com.insureflow.policy.dto;

import com.insureflow.policy.entity.BenefitCategory;
import com.insureflow.policy.entity.PlanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PolicyValidationRequest {

    @NotNull
    private PlanType plan;

    @NotNull
    private BenefitCategory category;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal requestedAmount;
}
