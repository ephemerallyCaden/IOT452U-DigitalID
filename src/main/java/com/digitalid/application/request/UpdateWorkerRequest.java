package com.digitalid.application.request;

import com.digitalid.application.port.in.Command;
import com.digitalid.domain.model.Region;

public class UpdateWorkerRequest implements Command {

    private final String workerId;
    private final String email;    // null means no change
    private final Region region;   // null means no change

    public UpdateWorkerRequest(String workerId, String email, Region region) {
        this.workerId = workerId;
        this.email = email;
        this.region = region;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getEmail() {
        return email;
    }

    public Region getRegion() {
        return region;
    }
}
