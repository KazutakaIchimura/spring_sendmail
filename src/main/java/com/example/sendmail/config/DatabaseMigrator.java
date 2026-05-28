package com.example.sendmail.config;

import com.example.sendmail.domain.entity.Staff;
import com.example.sendmail.domain.enums.Role;
import com.example.sendmail.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrator implements CommandLineRunner {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        insertAdminIfNotExists();
        addBuildingColumnIfNotExists();
    }

    private void insertAdminIfNotExists() {
        try {
            if (staffRepository.findByEmail("admin@example.com").isEmpty()) {
                Staff admin = new Staff();
                admin.setName("管理者");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(passwordEncoder.encode("changeme"));
                admin.setRole(Role.ADMIN);
                admin.setIsActive(true);
                admin.setForcePasswordChange(true);
                staffRepository.save(admin);
                log.info("Migration: initial admin user created");
            }
        } catch (Exception e) {
            log.warn("Migration: admin user creation skipped: {}", e.getMessage());
        }
    }

    private void addBuildingColumnIfNotExists() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                    Integer.class, "offices", "building");
            if (count == null || count == 0) {
                jdbcTemplate.execute(
                        "ALTER TABLE offices ADD COLUMN building VARCHAR(200) NULL AFTER postal_code");
                log.info("Migration: added column offices.building");
            }
        } catch (Exception e) {
            log.warn("Migration: building column skipped: {}", e.getMessage());
        }
    }
}
