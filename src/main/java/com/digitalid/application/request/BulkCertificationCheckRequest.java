package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;

import java.util.List;

public class BulkCertificationCheckRequest implements Query {

    private final List<String> workerIds;

    public BulkCertificationCheckRequest(List<String> workerIds) {
        this.workerIds = List.copyOf(workerIds);
    }

    public List<String> getWorkerIds() {
        return workerIds;
    }
}
