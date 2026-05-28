package com.example.sendmail.repository;

import com.example.sendmail.domain.entity.MailSend;
import com.example.sendmail.domain.enums.SendStatus;
import com.example.sendmail.domain.enums.SendType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MailSendRepository extends JpaRepository<MailSend, Long> {
    List<MailSend> findByOfficeId(Long officeId);
    List<MailSend> findByStatus(SendStatus status);
    boolean existsByUserIdAndOfficeIdAndSendTypeAndSendMonth(
            Long userId, Long officeId, SendType sendType, LocalDate sendMonth);
}
