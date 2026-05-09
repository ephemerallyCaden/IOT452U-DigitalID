package com.digitalid.application.service;

import java.util.ArrayList;
import java.util.List;

import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;

/**
 * Observable subject that maintains a list of WorkerLifecycleListeners
 * and notifies them when worker lifecycle events occur.
 * Any use case that modifies worker state can use this to broadcast changes.
 */
public class WorkerLifecycleNotifier {

    private final List<WorkerLifecycleListener> listeners = new ArrayList<>();

    public void addListener(WorkerLifecycleListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WorkerLifecycleListener listener) {
        listeners.remove(listener);
    }

    public void notifyStatusChanged(Worker worker, WorkerStatus previousStatus, WorkerStatus newStatus) {
        for (WorkerLifecycleListener listener : listeners) {
            listener.onStatusChanged(worker, previousStatus, newStatus);
        }
    }

    public void notifyWorkerCreated(Worker worker) {
        for (WorkerLifecycleListener listener : listeners) {
            listener.onWorkerCreated(worker);
        }
    }

    public void notifyWorkerDeleted(String workerId) {
        for (WorkerLifecycleListener listener : listeners) {
            listener.onWorkerDeleted(workerId);
        }
    }

    public int getListenerCount() {
        return listeners.size();
    }

}
