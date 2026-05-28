package com.example.sendmail.service;

import com.example.sendmail.domain.enums.SendStatus;
import com.example.sendmail.dto.response.DashboardResponse;
import com.example.sendmail.repository.MailSendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MailSendRepository mailSendRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        String currentMonth = thisMonth.format(MONTH_FORMATTER);

        var pending = mailSendRepository.findByStatus(SendStatus.PENDING);
        long pendingCount = pending.size();

        var overdue = pending.stream()
                .filter(ms -> ms.getSendMonth().isBefore(thisMonth))
                .toList();
        long overdueCount = overdue.size();

        List<DashboardResponse.OverdueMonthCount> overdueMonths = overdue.stream()
                .collect(Collectors.groupingBy(
                        ms -> ms.getSendMonth().format(MONTH_FORMATTER),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> DashboardResponse.OverdueMonthCount.builder()
                        .month(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        var sent = mailSendRepository.findByStatus(SendStatus.SENT);

        long sentThisMonthCount = sent.stream()
                .filter(ms -> ms.getSendMonth().equals(thisMonth))
                .count();

        List<DashboardResponse.RecentHistoryItem> recentHistory = sent.stream()
                .filter(ms -> ms.getBatch() != null)
                .sorted(Comparator.comparing(ms -> ms.getBatch().getSentAt(), Comparator.reverseOrder()))
                .limit(5)
                .map(ms -> DashboardResponse.RecentHistoryItem.builder()
                        .id(ms.getId())
                        .officeName(ms.getOffice().getName())
                        .userName(ms.getUser().getName())
                        .sendType(ms.getSendType())
                        .sentAt(ms.getBatch().getSentAt())
                        .build())
                .toList();

        return DashboardResponse.builder()
                .pendingCount(pendingCount)
                .overdueCount(overdueCount)
                .sentThisMonthCount(sentThisMonthCount)
                .currentMonth(currentMonth)
                .overdueMonths(overdueMonths)
                .recentHistory(recentHistory)
                .build();
    }
}
