package com.insureflow.policy.controller;

import com.insureflow.policy.dto.PolicyValidationRequest;
import com.insureflow.policy.dto.PolicyValidationResponse;
import com.insureflow.policy.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/policies")
@RequiredArgsConstructor
public class InternalPolicyController {

    private final PolicyService policyService;

    @PostMapping("/validate")
    public ResponseEntity<PolicyValidationResponse> validateCoverage(
            @Valid @RequestBody PolicyValidationRequest request
    ) {
        return ResponseEntity.ok(policyService.validateCoverage(request));
    }
}
