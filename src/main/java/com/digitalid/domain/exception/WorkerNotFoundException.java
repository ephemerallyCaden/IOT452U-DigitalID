package com.digitalid.domain.exception;

public class WorkerNotFoundException extends DomainException {

    public WorkerNotFoundException(String workerId) {
        super("Worker not found: " + workerId);
    }
}
