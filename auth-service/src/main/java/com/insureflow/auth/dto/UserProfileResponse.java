package com.insureflow.auth.dto;

import com.insureflow.auth.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private String username;
    private String email;
    private Set<Role> roles;
}
