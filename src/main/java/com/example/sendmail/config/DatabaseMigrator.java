package com.example.sendmail.config;

import com.example.sendmail.domain.entity.Staff;
import com.example.sendmail.domain.enums.Role;
import com.example.sendmail.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrator implements CommandLineRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_RETRIES = 10;
    private static final long RETRY_INTERVAL_MS = 3000;

    @Override
    public void run(String... args) throws InterruptedException {
        waitForDatabase();
        initSchema();
        insertAdminIfNotExists();
        addBuildingColumnIfNotExists();
    }

    private void waitForDatabase() throws InterruptedException {
        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                log.info("Migration: database connection established");
                return;
            } catch (Exception e) {
                log.warn("Migration: DB not ready (attempt {}/{}), retrying in {}ms...",
                        i, MAX_RETRIES, RETRY_INTERVAL_MS);
                Thread.sleep(RETRY_INTERVAL_MS);
            }
        }
        log.error("Migration: could not connect to database after {} retries", MAX_RETRIES);
    }

    private void initSchema() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("schema-mysql.sql"));
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            log.info("Migration: schema initialized");
        } catch (Exception e) {
            log.warn("Migration: schema init skipped: {}", e.getMessage());
        }
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
            } else {
                log.info("Migration: admin user already exists");
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
