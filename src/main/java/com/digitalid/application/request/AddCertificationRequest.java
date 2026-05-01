package com.digitalid.application.request;

import java.time.LocalDate;

import com.digitalid.application.port.in.Command;
import com.digitalid.domain.model.CertificationType;

public class AddCertificationRequest implements Command {

    private final String workerId;
    private final CertificationType certificationType;
    private final String issuingAuthority;
    private final String certificationNumber;
    private final LocalDate issueDate;
    private final LocalDate expirationDate; // null for lifetime certs

    public AddCertificationRequest(String workerId,
        CertificationType certificationType,
        String issuingAuthority,
        String certificationNumber,
        LocalDate issueDate,
        LocalDate expirationDate
    ) {
        this.workerId = workerId;
        this.certificationType = certificationType;
        this.issuingAuthority = issuingAuthority;
        this.certificationNumber = certificationNumber;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
    }

    public String getWorkerId() {
        return workerId;
    }

    public CertificationType getCertificationType() {
        return certificationType;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public String getCertificationNumber() {
        return certificationNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
