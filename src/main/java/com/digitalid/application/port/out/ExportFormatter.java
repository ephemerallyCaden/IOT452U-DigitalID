package com.digitalid.application.port.out;

import java.util.List;

import com.digitalid.domain.model.Worker;

public interface ExportFormatter {
    String format(List<Worker> workers);
}
