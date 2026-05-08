package com.digitalid.infrastructure.adapter.export;

import java.util.List;

import com.digitalid.application.port.out.ExportFormatter;
import com.digitalid.domain.model.Worker;

public class CsvExportFormatter implements ExportFormatter {

    @Override
    public String format(List<Worker> workers) {
        StringBuilder sb = new StringBuilder();
        sb.append("workerId,fullName,email,region,status\n");
        for (Worker w : workers) {
            sb.append(escapeCsv(w.getWorkerId())).append(",")
                    .append(escapeCsv(w.getFullName())).append(",")
                    .append(escapeCsv(w.getEmail())).append(",")
                    .append(w.getRegion().name()).append(",")
                    .append(w.getStatus().name()).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
