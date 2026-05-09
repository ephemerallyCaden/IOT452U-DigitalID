package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;
import com.digitalid.domain.model.Region;

public class GenerateReportRequest implements Query {

    private final Region region;

    public GenerateReportRequest(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }
}
