package com.digitalid.domain.exception;

public class UnauthorisedAccessException extends DomainException {

    public UnauthorisedAccessException(String organisationName, String toolName) {
        super("Organisation '" + organisationName + "' is not authorised to use " + toolName);
    }
}
