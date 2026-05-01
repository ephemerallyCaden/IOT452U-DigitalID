package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;

public class CheckExpiringCertsRequest implements Query {

    private final int withinDays;

    public CheckExpiringCertsRequest(int withinDays) {
        this.withinDays = withinDays;
    }

    public int getWithinDays() {
        return withinDays;
    }
}
