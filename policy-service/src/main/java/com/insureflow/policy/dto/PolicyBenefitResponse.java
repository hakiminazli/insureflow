package com.insureflow.policy.dto;

import com.insureflow.policy.entity.BenefitCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PolicyBenefitResponse {
    private BenefitCategory category;
    private BigDecimal coverageLimit;
    private BigDecimal copayAmount;
    private boolean excluded;
}
