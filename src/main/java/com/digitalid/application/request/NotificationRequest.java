package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;

public class NotificationRequest implements Command {

    private final String workerId;
    private final String message;

    public NotificationRequest(String workerId, String message) {
        this.workerId = workerId;
        this.message = message;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getMessage() {
        return message;
    }
}
