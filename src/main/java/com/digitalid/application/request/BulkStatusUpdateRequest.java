package com.digitalid.application.request;

import java.util.List;

import com.digitalid.application.port.in.Command;
import com.digitalid.domain.model.WorkerStatus;

public class BulkStatusUpdateRequest implements Command {

    private final List<String> workerIds;
    private final WorkerStatus newStatus;

    public BulkStatusUpdateRequest(List<String> workerIds, WorkerStatus newStatus) {
        this.workerIds = List.copyOf(workerIds);
        this.newStatus = newStatus;
    }

    public List<String> getWorkerIds() {
        return workerIds;
    }

    public WorkerStatus getNewStatus() {
        return newStatus;
    }

    public int count() {
        return workerIds.size();
    }
}
