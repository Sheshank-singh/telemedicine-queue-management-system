package com.tqms.repository;

import com.tqms.model.Priority;
import com.tqms.model.QueueRecord;
import com.tqms.model.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRecordRepository extends JpaRepository<QueueRecord, Long> {

    Optional<QueueRecord> findByTokenNo(String tokenNo);

    boolean existsByTokenNo(String tokenNo);

    List<QueueRecord> findAllByOrderByPriorityDescCreatedAtAsc();

    List<QueueRecord> findByStatus(QueueStatus status);

    List<QueueRecord> findByPatientNameContainingIgnoreCase(String name);

    @Query("SELECT q FROM QueueRecord q WHERE " +
           "(:query IS NULL OR LOWER(q.patientName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(q.tokenNo) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR q.status = :status) " +
           "ORDER BY q.priority DESC, q.createdAt ASC")
    List<QueueRecord> searchRecords(@Param("query") String query, @Param("status") QueueStatus status);

    long countByStatus(QueueStatus status);

    @Query("SELECT COUNT(q) FROM QueueRecord q WHERE q.status = 'WAITING' AND q.createdAt < :createdAt")
    long countWaitingAhead(@Param("createdAt") java.time.LocalDateTime createdAt);
}
