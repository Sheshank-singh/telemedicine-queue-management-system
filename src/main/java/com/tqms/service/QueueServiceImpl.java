package com.tqms.service;

import com.tqms.dto.QueueRecordRequest;
import com.tqms.dto.QueueSummaryResponse;
import com.tqms.dto.RegistrationResponseDto;
import com.tqms.model.Priority;
import com.tqms.model.QueueRecord;
import com.tqms.model.QueueStatus;
import com.tqms.model.User;
import com.tqms.repository.QueueRecordRepository;
import com.tqms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class QueueServiceImpl implements QueueService {

    private static final int MINUTES_PER_CONSULTATION = 12;

    private final QueueRecordRepository queueRecordRepository;
    private final UserRepository userRepository;

    @Autowired
    public QueueServiceImpl(QueueRecordRepository queueRecordRepository, UserRepository userRepository) {
        this.queueRecordRepository = queueRecordRepository;
        this.userRepository = userRepository;
    }

    private synchronized String generateNextToken() {
        long seq = queueRecordRepository.count() + 1;
        String prefix = "TQ-";
        String candidate = prefix + String.format("%03d", seq);
        while (queueRecordRepository.existsByTokenNo(candidate)) {
            seq++;
            candidate = prefix + String.format("%03d", seq);
        }
        return candidate;
    }

    @Override
    public QueueRecord createRecord(QueueRecordRequest request) {
        String tokenNo = generateNextToken();
        User creator = null;
        if (request.getCreatedById() != null) {
            creator = userRepository.findById(request.getCreatedById()).orElse(null);
        }

        QueueRecord record = new QueueRecord();
        record.setTokenNo(tokenNo);
        record.setPatientName(request.getPatientName().trim());
        record.setContact(request.getContact());
        record.setDepartment(request.getDepartment().trim());
        record.setSymptoms(request.getSymptoms());
        record.setPriority(request.getPriority() != null ? request.getPriority() : Priority.NORMAL);
        record.setStatus(QueueStatus.WAITING);
        record.setCreatedBy(creator);

        return queueRecordRepository.save(record);
    }

    @Override
    public RegistrationResponseDto registerPatient(QueueRecordRequest request) {
        QueueRecord saved = createRecord(request);
        long ahead = getPatientsAhead(saved.getTokenNo());
        int estimatedWait = calculateEstimatedWaitMinutes(saved.getTokenNo());

        return new RegistrationResponseDto(
                saved.getId(),
                saved.getTokenNo(),
                saved.getPatientName(),
                saved.getContact(),
                saved.getDepartment(),
                saved.getPriority(),
                saved.getStatus(),
                ahead,
                estimatedWait,
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueRecord> getAllRecords() {
        return queueRecordRepository.findAllByOrderByPriorityDescCreatedAtAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QueueRecord> getRecordById(Long id) {
        return queueRecordRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QueueRecord> getRecordByToken(String tokenNo) {
        return queueRecordRepository.findByTokenNo(tokenNo.trim().toUpperCase());
    }

    @Override
    public QueueRecord updateRecord(Long id, QueueRecordRequest request) {
        QueueRecord existing = queueRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Queue record not found with id: " + id));

        if (request.getPatientName() != null && !request.getPatientName().isBlank()) {
            existing.setPatientName(request.getPatientName().trim());
        }
        if (request.getContact() != null) {
            existing.setContact(request.getContact().trim());
        }
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            existing.setDepartment(request.getDepartment().trim());
        }
        if (request.getSymptoms() != null) {
            existing.setSymptoms(request.getSymptoms().trim());
        }
        if (request.getPriority() != null) {
            existing.setPriority(request.getPriority());
        }

        return queueRecordRepository.save(existing);
    }

    @Override
    public QueueRecord updateStatus(Long id, QueueStatus newStatus) {
        QueueRecord existing = queueRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Queue record not found with id: " + id));

        QueueStatus current = existing.getStatus();
        validateStatusTransition(current, newStatus);

        existing.setStatus(newStatus);
        return queueRecordRepository.save(existing);
    }

    private void validateStatusTransition(QueueStatus current, QueueStatus next) {
        if (current == next) return;

        if (current == QueueStatus.COMPLETED) {
            throw new IllegalStateException("Completed records cannot change status.");
        }
        if (current == QueueStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled records cannot change status.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueRecord> searchRecords(String query, QueueStatus status) {
        String cleanQuery = (query != null && !query.isBlank()) ? query.trim() : null;
        return queueRecordRepository.searchRecords(cleanQuery, status);
    }

    @Override
    @Transactional(readOnly = true)
    public QueueSummaryResponse getSummary() {
        long waiting = queueRecordRepository.countByStatus(QueueStatus.WAITING);
        long called = queueRecordRepository.countByStatus(QueueStatus.CALLED);
        long inConsult = queueRecordRepository.countByStatus(QueueStatus.IN_CONSULTATION);
        long completed = queueRecordRepository.countByStatus(QueueStatus.COMPLETED);
        long cancelled = queueRecordRepository.countByStatus(QueueStatus.CANCELLED);
        long noShow = queueRecordRepository.countByStatus(QueueStatus.NO_SHOW);
        long total = queueRecordRepository.count();

        Map<String, Long> breakdown = new LinkedHashMap<>();
        breakdown.put("WAITING", waiting);
        breakdown.put("CALLED", called);
        breakdown.put("IN_CONSULTATION", inConsult);
        breakdown.put("COMPLETED", completed);
        breakdown.put("CANCELLED", cancelled);
        breakdown.put("NO_SHOW", noShow);

        return new QueueSummaryResponse(total, waiting, called, inConsult, completed, cancelled, noShow, breakdown);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPatientsAhead(String tokenNo) {
        Optional<QueueRecord> recordOpt = queueRecordRepository.findByTokenNo(tokenNo.trim().toUpperCase());
        if (recordOpt.isEmpty() || recordOpt.get().getStatus() != QueueStatus.WAITING) {
            return 0;
        }
        return queueRecordRepository.countWaitingAhead(recordOpt.get().getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateEstimatedWaitMinutes(String tokenNo) {
        long ahead = getPatientsAhead(tokenNo);
        return (int) (ahead * MINUTES_PER_CONSULTATION);
    }
}
