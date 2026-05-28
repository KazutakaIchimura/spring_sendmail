package com.example.sendmail.dto.response;

import com.example.sendmail.domain.enums.SendType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private long pendingCount;
    private long overdueCount;
    private long sentThisMonthCount;
    private String currentMonth;
    private List<OverdueMonthCount> overdueMonths;
    private List<RecentHistoryItem> recentHistory;

    @Getter
    @Builder
    public static class OverdueMonthCount {
        private String month;
        private long count;
    }

    @Getter
    @Builder
    public static class RecentHistoryItem {
        private Long id;
        private String officeName;
        private String userName;
        private SendType sendType;
        private LocalDateTime sentAt;
    }
}
