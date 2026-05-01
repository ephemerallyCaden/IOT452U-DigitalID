package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;

import java.time.LocalDate;

public class RenewCertificationRequest implements Command {

    private final String workerId;
    private final String certificationId;
    private final LocalDate newIssueDate;
    private final LocalDate newExpirationDate;

    public RenewCertificationRequest(String workerId, String certificationId,
                                     LocalDate newIssueDate, LocalDate newExpirationDate) {
        this.workerId = workerId;
        this.certificationId = certificationId;
        this.newIssueDate = newIssueDate;
        this.newExpirationDate = newExpirationDate;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getCertificationId() {
        return certificationId;
    }

    public LocalDate getNewIssueDate() {
        return newIssueDate;
    }

    public LocalDate getNewExpirationDate() {
        return newExpirationDate;
    }
}
