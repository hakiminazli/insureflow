package com.insureflow.policy.dto;

import com.insureflow.policy.entity.PlanType;
import com.insureflow.policy.entity.PolicyStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreatePolicyRequest {

    @NotBlank
    private String policyNumber;

    @NotNull
    private PlanType plan;

    private PolicyStatus status = PolicyStatus.ACTIVE;

    @Valid
    @NotEmpty
    private List<PolicyBenefitRequest> benefits;
}
