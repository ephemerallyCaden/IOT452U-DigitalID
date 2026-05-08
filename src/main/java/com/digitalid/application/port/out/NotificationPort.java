package com.digitalid.application.port.out;

public interface NotificationPort {
    void send(String recipient, String email, String message);
}
