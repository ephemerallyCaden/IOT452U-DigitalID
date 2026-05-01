package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;

public class ViewWorkerRequest implements Query {

    private final String workerId;

    public ViewWorkerRequest(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerId() {
        return workerId;
    }
}
