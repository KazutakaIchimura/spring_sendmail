package com.example.sendmail.service;

import com.example.sendmail.domain.enums.SendStatus;
import com.example.sendmail.dto.response.DashboardResponse;
import com.example.sendmail.repository.MailSendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MailSendRepository mailSendRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);

        var pending = mailSendRepository.findByStatus(SendStatus.PENDING);
        long pendingCount = pending.size();
        long overdueCount = pending.stream()
                .filter(ms -> ms.getSendMonth().isBefore(thisMonth))
                .count();
        long sentThisMonthCount = mailSendRepository.findByStatus(SendStatus.SENT).stream()
                .filter(ms -> ms.getSendMonth().equals(thisMonth))
                .count();

        return DashboardResponse.builder()
                .pendingCount(pendingCount)
                .overdueCount(overdueCount)
                .sentThisMonthCount(sentThisMonthCount)
                .build();
    }
}
