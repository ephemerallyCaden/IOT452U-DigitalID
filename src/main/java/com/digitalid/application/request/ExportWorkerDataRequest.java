package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;
import com.digitalid.domain.model.Region;

public class ExportWorkerDataRequest implements Query {

    private final Region region;
    private final String format; // "JSON" or "CSV"

    public ExportWorkerDataRequest(Region region, String format) {
        this.region = region;
        this.format = format;
    }

    public Region getRegion() {
        return region;
    }

    public String getFormat() {
        return format;
    }
}
