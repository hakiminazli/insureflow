package com.insureflow.policy.controller;

import com.insureflow.policy.dto.CreatePolicyRequest;
import com.insureflow.policy.dto.PolicyResponse;
import com.insureflow.policy.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> getPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicy(id));
    }
}
