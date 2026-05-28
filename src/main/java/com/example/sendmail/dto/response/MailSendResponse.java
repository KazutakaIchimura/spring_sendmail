package com.example.sendmail.dto.response;

import com.example.sendmail.domain.entity.MailSend;
import com.example.sendmail.domain.enums.SendStatus;
import com.example.sendmail.domain.enums.SendType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MailSendResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long officeId;
    private String officeName;
    private SendType sendType;
    private LocalDate sendMonth;
    private SendStatus status;
    private boolean isOverdue;
    private Long batchId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MailSendResponse from(MailSend ms, LocalDate thisMonth) {
        boolean overdue = ms.getStatus() == SendStatus.PENDING
                && ms.getSendMonth().isBefore(thisMonth);
        return MailSendResponse.builder()
                .id(ms.getId())
                .userId(ms.getUser().getId())
                .userName(ms.getUser().getName())
                .officeId(ms.getOffice().getId())
                .officeName(ms.getOffice().getName())
                .sendType(ms.getSendType())
                .sendMonth(ms.getSendMonth())
                .status(ms.getStatus())
                .isOverdue(overdue)
                .batchId(ms.getBatch() != null ? ms.getBatch().getId() : null)
                .createdAt(ms.getCreatedAt())
                .updatedAt(ms.getUpdatedAt())
                .build();
    }
}
