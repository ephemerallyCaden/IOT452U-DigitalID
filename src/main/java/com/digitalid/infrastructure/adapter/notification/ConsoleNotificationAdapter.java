package com.digitalid.infrastructure.adapter.notification;

import com.digitalid.application.port.out.NotificationPort;

public class ConsoleNotificationAdapter implements NotificationPort {

    @Override
    public void send(String recipient, String email, String message) {
        System.out.println("[NOTIFICATION] " + recipient + " (" + email + "): " + message);
    }
}
