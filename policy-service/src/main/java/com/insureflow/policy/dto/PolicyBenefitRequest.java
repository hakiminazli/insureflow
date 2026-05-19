package com.insureflow.policy.dto;

import com.insureflow.policy.entity.BenefitCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PolicyBenefitRequest {

    @NotNull
    private BenefitCategory category;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal coverageLimit;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal copayAmount;

    private boolean excluded;
}
