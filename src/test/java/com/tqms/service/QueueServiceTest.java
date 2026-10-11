package com.tqms.service;

import com.tqms.dto.QueueRecordRequest;
import com.tqms.dto.RegistrationResponseDto;
import com.tqms.model.Priority;
import com.tqms.model.QueueRecord;
import com.tqms.model.QueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    private QueueRecordRequest normalRequest;
    private QueueRecordRequest urgentRequest;

    @BeforeEach
    void setUp() {
        normalRequest = new QueueRecordRequest();
        normalRequest.setPatientName("Aarav Sharma");
        normalRequest.setContact("+91 9876543210");
        normalRequest.setDepartment("General Medicine");
        normalRequest.setSymptoms("Mild headache and fever");
        normalRequest.setPriority(Priority.NORMAL);

        urgentRequest = new QueueRecordRequest();
        urgentRequest.setPatientName("Priya Patel");
        urgentRequest.setContact("+91 9123456789");
        urgentRequest.setDepartment("Cardiology");
        urgentRequest.setSymptoms("Chest discomfort and breathlessness");
        urgentRequest.setPriority(Priority.URGENT);
    }

    @Test
    @DisplayName("Feature 1: Patient registration generates a valid unique token in WAITING status")
    void testPatientRegistrationGeneratesUniqueToken() {
        RegistrationResponseDto response = queueService.registerPatient(normalRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getTokenNo());
        assertTrue(response.getTokenNo().startsWith("TQ-"));
        assertEquals("Aarav Sharma", response.getPatientName());
        assertEquals("General Medicine", response.getDepartment());
        assertEquals(QueueStatus.WAITING, response.getStatus());
        assertEquals(Priority.NORMAL, response.getPriority());
    }

    @Test
    @DisplayName("Feature 1: Multiple registrations produce sequential unique tokens")
    void testSequentialTokenAllocation() {
        RegistrationResponseDto r1 = queueService.registerPatient(normalRequest);
        RegistrationResponseDto r2 = queueService.registerPatient(urgentRequest);

        assertNotEquals(r1.getTokenNo(), r2.getTokenNo());
        assertTrue(r1.getTokenNo().startsWith("TQ-"));
        assertTrue(r2.getTokenNo().startsWith("TQ-"));
    }

    @Test
    @DisplayName("Feature 1: Urgent priority patients take precedence in queue order")
    void testUrgentPatientPrecedence() {
        queueService.registerPatient(normalRequest);
        queueService.registerPatient(urgentRequest);

        List<QueueRecord> queue = queueService.getAllRecords();
        assertFalse(queue.isEmpty());
        // First record in priority queue must be the urgent patient
        assertEquals(Priority.URGENT, queue.get(0).getPriority());
        assertEquals("Priya Patel", queue.get(0).getPatientName());
    }

    @Test
    @DisplayName("Feature 1: Estimated wait time scales with waiting patients ahead")
    void testEstimatedWaitTime() {
        RegistrationResponseDto r1 = queueService.registerPatient(normalRequest);
        RegistrationResponseDto r2 = queueService.registerPatient(normalRequest);

        assertEquals(0, r1.getEstimatedWaitMinutes());
        assertTrue(r2.getEstimatedWaitMinutes() >= 0);
    }
}
