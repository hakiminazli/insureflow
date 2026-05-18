package com.insureflow.member.dto;

import com.insureflow.member.entity.MemberStatus;
import com.insureflow.member.entity.PlanType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMemberRequest {

    @NotBlank
    private String memberNumber;

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotNull
    private PlanType plan;

    private MemberStatus status = MemberStatus.ACTIVE;

    @NotNull
    private LocalDate coverageStartDate;

    @NotNull
    private LocalDate coverageEndDate;
}
