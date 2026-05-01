package com.digitalid.application.request;

import java.time.LocalDate;

import com.digitalid.application.port.in.Command;
import com.digitalid.domain.model.Region;

public class CreateWorkerRequest implements Command {

    private final String fullName;
    private final LocalDate dateOfBirth;
    private final String email;
    private final Region region;

    public CreateWorkerRequest(String fullName, LocalDate dateOfBirth, String email, Region region) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.region = region;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "CreateWorkerRequest{name='" + fullName + "', region=" + region + "}";
    }
}
