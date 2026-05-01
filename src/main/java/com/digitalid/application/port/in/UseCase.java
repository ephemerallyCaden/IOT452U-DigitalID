package com.digitalid.application.port.in;

public interface UseCase<Request, Response> {
    Response execute(Request request);
}
