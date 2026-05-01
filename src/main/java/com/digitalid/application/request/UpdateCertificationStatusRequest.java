package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;

public class UpdateCertificationStatusRequest implements Command {

    private final String workerId;
    private final String certificationId;
    private final String newStatus; // ACTIVE, SUSPENDED, or EXPIRED

    public UpdateCertificationStatusRequest(String workerId, String certificationId,
                                            String newStatus) {
        this.workerId = workerId;
        this.certificationId = certificationId;
        this.newStatus = newStatus;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getCertificationId() {
        return certificationId;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
