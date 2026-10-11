package com.tqms.service;

import com.tqms.dto.QueueRecordRequest;
import com.tqms.dto.QueueSummaryResponse;
import com.tqms.model.QueueRecord;
import com.tqms.model.QueueStatus;

import java.util.List;
import java.util.Optional;

public interface QueueService {

    QueueRecord createRecord(QueueRecordRequest request);

    List<QueueRecord> getAllRecords();

    Optional<QueueRecord> getRecordById(Long id);

    Optional<QueueRecord> getRecordByToken(String tokenNo);

    QueueRecord updateRecord(Long id, QueueRecordRequest request);

    QueueRecord updateStatus(Long id, QueueStatus newStatus);

    List<QueueRecord> searchRecords(String query, QueueStatus status);

    QueueSummaryResponse getSummary();

    long getPatientsAhead(String tokenNo);
}
