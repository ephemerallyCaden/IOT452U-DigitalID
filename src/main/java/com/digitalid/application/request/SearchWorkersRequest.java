package com.digitalid.application.request;

import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.WorkerStatus;
import com.digitalid.application.port.in.Query;

// All fields are optional filters (null = don't filter by that field)
public class SearchWorkersRequest implements Query {

    private final String nameQuery;
    private final Region region;
    private final WorkerStatus status;

    public SearchWorkersRequest(String nameQuery, Region region, WorkerStatus status) {
        this.nameQuery = nameQuery;
        this.region = region;
        this.status = status;
    }

    public String getNameQuery() {
        return nameQuery;
    }

    public Region getRegion() {
        return region;
    }

    public WorkerStatus getStatus() {
        return status;
    }
}
