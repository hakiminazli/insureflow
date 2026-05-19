package com.insureflow.policy.service;

import com.insureflow.policy.dto.CreatePolicyRequest;
import com.insureflow.policy.dto.PolicyBenefitResponse;
import com.insureflow.policy.dto.PolicyValidationRequest;
import com.insureflow.policy.dto.PolicyValidationResponse;
import com.insureflow.policy.dto.PolicyResponse;
import com.insureflow.policy.entity.BenefitCategory;
import com.insureflow.policy.entity.Policy;
import com.insureflow.policy.entity.PolicyBenefit;
import com.insureflow.policy.entity.PolicyStatus;
import com.insureflow.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyResponse createPolicy(CreatePolicyRequest request) {
        if (policyRepository.existsByPolicyNumber(request.getPolicyNumber())) {
            throw new IllegalArgumentException("Policy number already exists");
        }

        if (policyRepository.existsByPlan(request.getPlan())) {
            throw new IllegalArgumentException("Policy already exists for plan " + request.getPlan());
        }

        validateUniqueBenefitCategories(request.getBenefits().stream()
                .map(benefit -> benefit.getCategory())
                .collect(Collectors.toList()));

        Policy policy = Policy.builder()
                .policyNumber(request.getPolicyNumber())
                .plan(request.getPlan())
                .status(request.getStatus() == null ? PolicyStatus.ACTIVE : request.getStatus())
                .build();

        List<PolicyBenefit> benefits = request.getBenefits().stream()
                .map(benefit -> PolicyBenefit.builder()
                        .policy(policy)
                        .category(benefit.getCategory())
                        .coverageLimit(benefit.getCoverageLimit())
                        .copayAmount(benefit.getCopayAmount())
                        .excluded(benefit.isExcluded())
                        .build())
                .toList();

        policy.getBenefits().addAll(benefits);

        return toResponse(policyRepository.save(policy));
    }

    public PolicyResponse getPolicy(Long id) {
        return toResponse(findPolicy(id));
    }

    public PolicyValidationResponse validateCoverage(PolicyValidationRequest request) {
        Policy policy = policyRepository.findByPlan(request.getPlan())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found for plan " + request.getPlan()));

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            return validationResponse(request, false, null, "Policy is not active");
        }

        PolicyBenefit benefit = policy.getBenefits().stream()
                .filter(item -> item.getCategory() == request.getCategory())
                .findFirst()
                .orElse(null);

        if (benefit == null) {
            return validationResponse(request, false, null, "Benefit is not configured for plan");
        }

        if (benefit.isExcluded()) {
            return validationResponse(request, false, benefit, "Benefit is excluded");
        }

        if (request.getRequestedAmount().compareTo(benefit.getCoverageLimit()) > 0) {
            return validationResponse(request, false, benefit, "Requested amount exceeds coverage limit");
        }

        return validationResponse(request, true, benefit, "Benefit is covered");
    }

    private Policy findPolicy(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
    }

    private void validateUniqueBenefitCategories(List<BenefitCategory> categories) {
        Set<BenefitCategory> uniqueCategories = EnumSet.noneOf(BenefitCategory.class);

        for (BenefitCategory category : categories) {
            if (!uniqueCategories.add(category)) {
                throw new IllegalArgumentException("Benefit category is duplicated: " + category);
            }
        }
    }

    private PolicyResponse toResponse(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .plan(policy.getPlan())
                .status(policy.getStatus())
                .benefits(policy.getBenefits().stream()
                        .map(this::toBenefitResponse)
                        .toList())
                .build();
    }

    private PolicyBenefitResponse toBenefitResponse(PolicyBenefit benefit) {
        return PolicyBenefitResponse.builder()
                .category(benefit.getCategory())
                .coverageLimit(benefit.getCoverageLimit())
                .copayAmount(benefit.getCopayAmount())
                .excluded(benefit.isExcluded())
                .build();
    }

    private PolicyValidationResponse validationResponse(
            PolicyValidationRequest request,
            boolean covered,
            PolicyBenefit benefit,
            String reason
    ) {
        return PolicyValidationResponse.builder()
                .covered(covered)
                .plan(request.getPlan())
                .category(request.getCategory())
                .requestedAmount(request.getRequestedAmount())
                .coverageLimit(benefit == null ? BigDecimal.ZERO : benefit.getCoverageLimit())
                .copayAmount(benefit == null ? BigDecimal.ZERO : benefit.getCopayAmount())
                .reason(reason)
                .build();
    }
}
