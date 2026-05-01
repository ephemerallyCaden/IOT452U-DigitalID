package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;
import com.digitalid.domain.model.ToolType;

import java.util.Map;

/**
 * Request for verifying a worker. The verificationLevel determines which
 * type of verification is performed (basic, with certs, with conditions, etc.)
 */
public class VerifyWorkerRequest implements Query {

    private final String workerId;
    private final ToolType verificationLevel;
    private final Map<String, String> conditions;

    public VerifyWorkerRequest(String workerId, ToolType verificationLevel) {
        this(workerId, verificationLevel, Map.of());
    }

    public VerifyWorkerRequest(String workerId, ToolType verificationLevel,
                               Map<String, String> conditions) {
        this.workerId = workerId;
        this.verificationLevel = verificationLevel;
        this.conditions = conditions != null ? Map.copyOf(conditions) : Map.of();
    }

    public String getWorkerId() {
        return workerId;
    }

    public ToolType getVerificationLevel() {
        return verificationLevel;
    }

    public Map<String, String> getConditions() {
        return conditions;
    }
}
