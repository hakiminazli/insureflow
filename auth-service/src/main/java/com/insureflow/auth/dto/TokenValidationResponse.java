package com.insureflow.auth.dto;

import com.insureflow.auth.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private Set<Role> roles;
}
