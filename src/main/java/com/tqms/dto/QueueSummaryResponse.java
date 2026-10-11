package com.tqms.dto;

import java.util.Map;

public class QueueSummaryResponse {

    private long totalRecords;
    private long waitingCount;
    private long calledCount;
    private long inConsultationCount;
    private long completedCount;
    private long cancelledCount;
    private long noShowCount;
    private Map<String, Long> statusBreakdown;

    public QueueSummaryResponse() {}

    public QueueSummaryResponse(long totalRecords, long waitingCount, long calledCount,
                                long inConsultationCount, long completedCount,
                                long cancelledCount, long noShowCount,
                                Map<String, Long> statusBreakdown) {
        this.totalRecords = totalRecords;
        this.waitingCount = waitingCount;
        this.calledCount = calledCount;
        this.inConsultationCount = inConsultationCount;
        this.completedCount = completedCount;
        this.cancelledCount = cancelledCount;
        this.noShowCount = noShowCount;
        this.statusBreakdown = statusBreakdown;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public long getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(long waitingCount) {
        this.waitingCount = waitingCount;
    }

    public long getCalledCount() {
        return calledCount;
    }

    public void setCalledCount(long calledCount) {
        this.calledCount = calledCount;
    }

    public long getInConsultationCount() {
        return inConsultationCount;
    }

    public void setInConsultationCount(long inConsultationCount) {
        this.inConsultationCount = inConsultationCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }

    public long getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(long cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public long getNoShowCount() {
        return noShowCount;
    }

    public void setNoShowCount(long noShowCount) {
        this.noShowCount = noShowCount;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }
}
