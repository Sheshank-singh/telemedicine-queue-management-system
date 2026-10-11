package com.tqms.dto;

import com.tqms.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QueueRecordRequest {

    @NotBlank(message = "Patient name is required")
    @Size(max = 80, message = "Patient name cannot exceed 80 characters")
    private String patientName;

    @Size(max = 15, message = "Contact cannot exceed 15 characters")
    private String contact;

    @NotBlank(message = "Department is required")
    @Size(max = 40, message = "Department cannot exceed 40 characters")
    private String department;

    @Size(max = 255, message = "Symptoms cannot exceed 255 characters")
    private String symptoms;

    private Priority priority = Priority.NORMAL;

    private Long createdById;

    public QueueRecordRequest() {}

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

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
}
