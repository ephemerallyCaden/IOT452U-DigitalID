package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;

public class SearchByExpirationRequest implements Query {

    private final int withinDays;

    public SearchByExpirationRequest(int withinDays) {
        this.withinDays = withinDays;
    }

    public int getWithinDays() {
        return withinDays;
    }
}
