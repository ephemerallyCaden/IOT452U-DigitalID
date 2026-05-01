package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;

public class NotificationRequest implements Command {

    private final String workerId;
    private final String notificationType; // "RENEWAL_REMINDER" or "STATUS_CHANGE"
    private final String message;

    public NotificationRequest(String workerId, String notificationType, String message) {
        this.workerId = workerId;
        this.notificationType = notificationType;
        this.message = message;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }
}
