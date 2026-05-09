package com.digitalid.application.service;

import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;

/**
 * Observer interface for worker lifecycle events.
 * Implementations receive notifications when significant worker state changes occur.
 * Each method has a default no-op so listeners only override the events they care about.
 */
public interface WorkerLifecycleListener {

    void onStatusChanged(Worker worker, WorkerStatus previousStatus, WorkerStatus newStatus);

    default void onWorkerCreated(Worker worker) {}

    default void onWorkerDeleted(String workerId) {}

}
