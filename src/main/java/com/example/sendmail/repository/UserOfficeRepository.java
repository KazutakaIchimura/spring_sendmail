package com.example.sendmail.repository;

import com.example.sendmail.domain.entity.UserOffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserOfficeRepository extends JpaRepository<UserOffice, Long> {
    List<UserOffice> findByUserId(Long userId);
    Optional<UserOffice> findByUserIdAndOfficeId(Long userId, Long officeId);
    boolean existsByUserIdAndOfficeId(Long userId, Long officeId);
}
