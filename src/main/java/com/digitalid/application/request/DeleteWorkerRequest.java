package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;

public class DeleteWorkerRequest implements Command {

    private final String workerId;

    public DeleteWorkerRequest(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerId() {
        return workerId;
    }
}
