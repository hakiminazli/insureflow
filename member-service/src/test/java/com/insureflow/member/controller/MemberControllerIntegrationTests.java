package com.insureflow.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insureflow.member.dto.CreateMemberRequest;
import com.insureflow.member.dto.MemberResponse;
import com.insureflow.member.entity.MemberStatus;
import com.insureflow.member.entity.PlanType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReadAndCheckEligibleMember() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest();
        request.setMemberNumber("MBR-1001");
        request.setFullName("Ali Provider");
        request.setEmail("ali.member@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setPlan(PlanType.GOLD);
        request.setStatus(MemberStatus.ACTIVE);
        request.setCoverageStartDate(LocalDate.now().minusDays(10));
        request.setCoverageEndDate(LocalDate.now().plusDays(10));

        String createResponse = mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberNumber").value("MBR-1001"))
                .andExpect(jsonPath("$.plan").value("GOLD"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long memberId = objectMapper.readValue(createResponse, MemberResponse.class).getId();

        mockMvc.perform(get("/api/members/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberNumber").value("MBR-1001"))
                .andExpect(jsonPath("$.fullName").value("Ali Provider"));

        mockMvc.perform(get("/internal/members/{id}/eligibility", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.plan").value("GOLD"))
                .andExpect(jsonPath("$.reason").value("Member is eligible"));
    }

    @Test
    void rejectsDuplicateMemberNumber() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest();
        request.setMemberNumber("MBR-2001");
        request.setFullName("Duplicate Member");
        request.setEmail("duplicate1@example.com");
        request.setDateOfBirth(LocalDate.of(1991, 2, 2));
        request.setPlan(PlanType.SILVER);
        request.setCoverageStartDate(LocalDate.now().minusDays(1));
        request.setCoverageEndDate(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        request.setEmail("duplicate2@example.com");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Member number already exists"));
    }
}
