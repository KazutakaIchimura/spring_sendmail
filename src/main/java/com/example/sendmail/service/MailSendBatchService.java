package com.example.sendmail.service;

import com.example.sendmail.domain.entity.MailSend;
import com.example.sendmail.domain.entity.MailSendBatch;
import com.example.sendmail.domain.entity.Staff;
import com.example.sendmail.domain.enums.SendStatus;
import com.example.sendmail.dto.request.CreateMailSendBatchRequest;
import com.example.sendmail.dto.response.MailSendBatchResponse;
import com.example.sendmail.exception.ResourceNotFoundException;
import com.example.sendmail.repository.MailSendBatchRepository;
import com.example.sendmail.repository.MailSendRepository;
import com.example.sendmail.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailSendBatchService {

    private final MailSendBatchRepository mailSendBatchRepository;
    private final MailSendRepository mailSendRepository;
    private final StaffRepository staffRepository;

    @Transactional
    public MailSendBatchResponse createBatch(CreateMailSendBatchRequest req, String currentUserEmail) {
        Staff sentBy = staffRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("スタッフが見つかりません: " + currentUserEmail));

        MailSendBatch batch = new MailSendBatch();
        batch.setSentBy(sentBy);
        batch.setSentAt(LocalDateTime.now());
        batch.setNotes(req.getNotes());
        MailSendBatch savedBatch = mailSendBatchRepository.save(batch);

        List<MailSend> mailSends = mailSendRepository.findAllById(req.getMailSendIds());
        mailSends.forEach(ms -> {
            ms.setStatus(SendStatus.SENT);
            ms.setBatch(savedBatch);
        });
        mailSendRepository.saveAll(mailSends);

        return MailSendBatchResponse.builder()
                .batchId(savedBatch.getId())
                .sentAt(savedBatch.getSentAt())
                .updatedCount(mailSends.size())
                .notes(savedBatch.getNotes())
                .build();
    }

    @Transactional(readOnly = true)
    public MailSendBatchResponse getBatch(Long id) {
        MailSendBatch batch = mailSendBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("バッチが見つかりません: " + id));
        long count = mailSendRepository.findAll().stream()
                .filter(ms -> ms.getBatch() != null && ms.getBatch().getId().equals(id))
                .count();
        return MailSendBatchResponse.builder()
                .batchId(batch.getId())
                .sentAt(batch.getSentAt())
                .updatedCount((int) count)
                .notes(batch.getNotes())
                .build();
    }
}
