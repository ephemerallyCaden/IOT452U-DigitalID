package com.digitalid.domain.exception;

public class WorkerNotFoundException extends EntityNotFoundException {

    public WorkerNotFoundException(String workerId) {
        super("Worker", workerId);
    }
}
