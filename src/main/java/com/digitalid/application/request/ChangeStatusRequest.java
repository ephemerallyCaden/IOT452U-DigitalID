package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;
import com.digitalid.domain.model.WorkerStatus;

public class ChangeStatusRequest implements Command {

    private final String workerId;
    private final WorkerStatus newStatus;

    public ChangeStatusRequest(String workerId, WorkerStatus newStatus) {
        this.workerId = workerId;
        this.newStatus = newStatus;
    }

    public String getWorkerId() {
        return workerId;
    }

    public WorkerStatus getNewStatus() {
        return newStatus;
    }
}
