package com.digitalid.domain.model;

import java.util.List;

public class VerificationResult {

    private final String workerId;
    private final boolean valid;
    private final WorkerStatus status;
    private final String message;
    private final List<Certification> certifications;

    private VerificationResult(Builder builder) {
        this.workerId = builder.workerId;
        this.valid = builder.valid;
        this.status = builder.status;
        this.message = builder.message;
        this.certifications = builder.certifications;
    }

    public String getWorkerId() { return workerId; }
    public boolean isValid() { return valid; }
    public WorkerStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Certification> getCertifications() { return certifications; }

    public static Builder builder(String workerId) {
        return new Builder(workerId);
    }

    public static class Builder {
        private final String workerId;
        private boolean valid;
        private WorkerStatus status;
        private String message;
        private List<Certification> certifications;

        Builder(String workerId) {
            this.workerId = workerId;
        }

        public Builder valid(boolean valid) { this.valid = valid; return this; }
        public Builder status(WorkerStatus status) { this.status = status; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder certifications(List<Certification> certs) { this.certifications = certs; return this; }

        public VerificationResult build() {
            return new VerificationResult(this);
        }
    }
}
