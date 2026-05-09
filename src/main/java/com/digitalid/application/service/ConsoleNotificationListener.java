package com.digitalid.application.service;

import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;

/**
 * A concrete observer that prints worker lifecycle notifications to the console.
 * In a production system this could send emails, push notifications, or webhook calls.
 */
public class ConsoleNotificationListener implements WorkerLifecycleListener {

    @Override
    public void onStatusChanged(Worker worker, WorkerStatus previousStatus, WorkerStatus newStatus) {
        System.out.println("[NOTIFICATION] Worker " + worker.getWorkerId()
                + " (" + worker.getFullName() + ")"
                + " status changed: " + previousStatus.getDisplayName()
                + " -> " + newStatus.getDisplayName());
    }

    @Override
    public void onWorkerCreated(Worker worker) {
        System.out.println("[NOTIFICATION] New worker registered: " + worker.getWorkerId()
                + " (" + worker.getFullName() + ") in " + worker.getRegion().getDisplayName());
    }

    @Override
    public void onWorkerDeleted(String workerId) {
        System.out.println("[NOTIFICATION] Worker removed from system: " + workerId);
    }

}
