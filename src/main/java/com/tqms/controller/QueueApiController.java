package com.tqms.controller;

import com.tqms.dto.QueueRecordRequest;
import com.tqms.dto.QueueSummaryResponse;
import com.tqms.dto.RegistrationResponseDto;
import com.tqms.model.QueueRecord;
import com.tqms.model.QueueStatus;
import com.tqms.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QueueApiController {

    private final QueueService queueService;

    @Autowired
    public QueueApiController(QueueService queueService) {
        this.queueService = queueService;
    }

    // POST /api/records - Register a patient and generate unique token (FR-01 Feature 1)
    @PostMapping("/records")
    public ResponseEntity<RegistrationResponseDto> registerPatient(@Valid @RequestBody QueueRecordRequest request) {
        RegistrationResponseDto response = queueService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/records - List the queue (FR-02)
    @GetMapping("/records")
    public ResponseEntity<List<QueueRecord>> getAllRecords() {
        return ResponseEntity.ok(queueService.getAllRecords());
    }

    // GET /api/records/{id} - View one record
    @GetMapping("/records/{id}")
    public ResponseEntity<QueueRecord> getRecordById(@PathVariable Long id) {
        return queueService.getRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/records/{id} - Update a record (FR-03)
    @PutMapping("/records/{id}")
    public ResponseEntity<QueueRecord> updateRecord(@PathVariable Long id, @RequestBody QueueRecordRequest request) {
        try {
            QueueRecord updated = queueService.updateRecord(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/records/search - Search by name, token or status (FR-04)
    @GetMapping("/records/search")
    public ResponseEntity<List<QueueRecord>> searchRecords(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) QueueStatus status) {
        return ResponseEntity.ok(queueService.searchRecords(query, status));
    }

    // PATCH /api/records/{id}/status - Change status (FR-05)
    @PatchMapping("/records/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam QueueStatus status) {
        try {
            QueueRecord updated = queueService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET /api/public/status/{token} - Patient token lookup (FR-07)
    @GetMapping("/public/status/{token}")
    public ResponseEntity<?> getPublicStatus(@PathVariable String token) {
        return queueService.getRecordByToken(token)
                .map(record -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("tokenNo", record.getTokenNo());
                    response.put("patientName", record.getPatientName());
                    response.put("department", record.getDepartment());
                    response.put("status", record.getStatus());
                    response.put("priority", record.getPriority());
                    response.put("patientsAhead", queueService.getPatientsAhead(token));
                    response.put("estimatedWaitMinutes", queueService.calculateEstimatedWaitMinutes(token));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/dashboard/summary - Status counts (FR-08)
    @GetMapping("/dashboard/summary")
    public ResponseEntity<QueueSummaryResponse> getSummary() {
        return ResponseEntity.ok(queueService.getSummary());
    }
}
