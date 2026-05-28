package com.example.sendmail.dto.response;

import com.example.sendmail.domain.entity.Staff;
import com.example.sendmail.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean isActive;
    private Boolean forcePasswordChange;
    private LocalDateTime createdAt;

    public static StaffResponse from(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .name(staff.getName())
                .email(staff.getEmail())
                .role(staff.getRole())
                .isActive(staff.getIsActive())
                .forcePasswordChange(staff.getForcePasswordChange())
                .createdAt(staff.getCreatedAt())
                .build();
    }
}
