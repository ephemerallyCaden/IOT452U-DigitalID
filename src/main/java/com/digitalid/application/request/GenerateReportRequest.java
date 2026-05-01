package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;
import com.digitalid.domain.model.Region;

public class GenerateReportRequest implements Query {

    private final String reportType; // "COMPLIANCE", "REGIONAL", "ORGANISATION_ACTIVITY"
    private final Region region;

    public GenerateReportRequest(String reportType, Region region) {
        this.reportType = reportType;
        this.region = region;
    }

    public String getReportType() {
        return reportType;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "GenerateReportRequest{type='" + reportType + "', region=" + region + "}";
    }
}
