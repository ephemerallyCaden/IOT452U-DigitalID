package com.digitalid.domain.exception;

public class CertificationExpiredException extends DomainException {

    public CertificationExpiredException(String certificationId) {
        super("Certification has expired: " + certificationId);
    }
}
