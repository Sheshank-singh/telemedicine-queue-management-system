package com.tqms.dto;

import com.tqms.model.Priority;
import com.tqms.model.QueueStatus;
import java.time.LocalDateTime;

public class RegistrationResponseDto {

    private Long id;
    private String tokenNo;
    private String patientName;
    private String contact;
    private String department;
    private Priority priority;
    private QueueStatus status;
    private long patientsAhead;
    private int estimatedWaitMinutes;
    private LocalDateTime createdAt;

    public RegistrationResponseDto() {}

    public RegistrationResponseDto(Long id, String tokenNo, String patientName, String contact,
                                   String department, Priority priority, QueueStatus status,
                                   long patientsAhead, int estimatedWaitMinutes, LocalDateTime createdAt) {
        this.id = id;
        this.tokenNo = tokenNo;
        this.patientName = patientName;
        this.contact = contact;
        this.department = department;
        this.priority = priority;
        this.status = status;
        this.patientsAhead = patientsAhead;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenNo() {
        return tokenNo;
    }

    public void setTokenNo(String tokenNo) {
        this.tokenNo = tokenNo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public long getPatientsAhead() {
        return patientsAhead;
    }

    public void setPatientsAhead(long patientsAhead) {
        this.patientsAhead = patientsAhead;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
