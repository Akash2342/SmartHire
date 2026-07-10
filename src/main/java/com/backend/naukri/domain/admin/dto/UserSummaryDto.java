package com.backend.naukri.domain.admin.dto;

import com.backend.naukri.common.enums.Role;
import com.backend.naukri.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/** Summary of a user shown in the admin user list. */
@Data
public class UserSummaryDto {

    private UUID id;
    private String email;
    private Role role;
    private boolean isActive;
    private boolean isEmailVerified;
    private LocalDateTime createdAt;

    public static UserSummaryDto from(User user) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
