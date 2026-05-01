package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;
import com.digitalid.domain.model.CertificationType;

public class SearchByCertificationRequest implements Query {

    private final CertificationType certificationType;

    public SearchByCertificationRequest(CertificationType certificationType) {
        this.certificationType = certificationType;
    }

    public CertificationType getCertificationType() {
        return certificationType;
    }
}
