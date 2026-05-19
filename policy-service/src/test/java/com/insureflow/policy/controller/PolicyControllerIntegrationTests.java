package com.insureflow.policy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insureflow.policy.dto.CreatePolicyRequest;
import com.insureflow.policy.dto.PolicyBenefitRequest;
import com.insureflow.policy.dto.PolicyResponse;
import com.insureflow.policy.dto.PolicyValidationRequest;
import com.insureflow.policy.entity.BenefitCategory;
import com.insureflow.policy.entity.PlanType;
import com.insureflow.policy.entity.PolicyStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PolicyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReadAndValidateCoveredPolicy() throws Exception {
        CreatePolicyRequest createRequest = createPolicy("POL-GOLD-1001", PlanType.GOLD);

        String createResponse = mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyNumber").value("POL-GOLD-1001"))
                .andExpect(jsonPath("$.plan").value("GOLD"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long policyId = objectMapper.readValue(createResponse, PolicyResponse.class).getId();

        mockMvc.perform(get("/api/policies/{id}", policyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyNumber").value("POL-GOLD-1001"))
                .andExpect(jsonPath("$.benefits[0].category").value("SURGERY"));

        PolicyValidationRequest validationRequest = new PolicyValidationRequest();
        validationRequest.setPlan(PlanType.GOLD);
        validationRequest.setCategory(BenefitCategory.SURGERY);
        validationRequest.setRequestedAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/internal/policies/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.covered").value(true))
                .andExpect(jsonPath("$.coverageLimit").value(5000.00))
                .andExpect(jsonPath("$.copayAmount").value(50.00))
                .andExpect(jsonPath("$.reason").value("Benefit is covered"));
    }

    @Test
    void validationRejectsExcludedBenefit() throws Exception {
        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPolicy("POL-PLATINUM-2001", PlanType.PLATINUM))))
                .andExpect(status().isCreated());

        PolicyValidationRequest validationRequest = new PolicyValidationRequest();
        validationRequest.setPlan(PlanType.PLATINUM);
        validationRequest.setCategory(BenefitCategory.THERAPY);
        validationRequest.setRequestedAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/internal/policies/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.covered").value(false))
                .andExpect(jsonPath("$.reason").value("Benefit is excluded"));
    }

    private CreatePolicyRequest createPolicy(String policyNumber, PlanType plan) {
        PolicyBenefitRequest surgery = new PolicyBenefitRequest();
        surgery.setCategory(BenefitCategory.SURGERY);
        surgery.setCoverageLimit(new BigDecimal("5000.00"));
        surgery.setCopayAmount(new BigDecimal("50.00"));
        surgery.setExcluded(false);

        PolicyBenefitRequest therapy = new PolicyBenefitRequest();
        therapy.setCategory(BenefitCategory.THERAPY);
        therapy.setCoverageLimit(new BigDecimal("0.00"));
        therapy.setCopayAmount(new BigDecimal("0.00"));
        therapy.setExcluded(true);

        CreatePolicyRequest request = new CreatePolicyRequest();
        request.setPolicyNumber(policyNumber);
        request.setPlan(plan);
        request.setStatus(PolicyStatus.ACTIVE);
        request.setBenefits(List.of(surgery, therapy));
        return request;
    }
}
