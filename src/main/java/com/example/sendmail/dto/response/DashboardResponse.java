package com.example.sendmail.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {
    private long pendingCount;
    private long overdueCount;
    private long sentThisMonthCount;
}
