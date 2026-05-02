package com.digitalid.infrastructure.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataStoreInitialiser {

    private final DatabaseConnection connection;

    public DataStoreInitialiser(DatabaseConnection connection) {
        this.connection = connection;
    }

    public void migrate() {
        createFileIfMissing("workers.json", "[]");
        createFileIfMissing("certifications.json", "[]");
        createFileIfMissing("work_authorisations.json", "[]");
        createFileIfMissing("audit_log.json", "[]");
        createFileIfMissing("sequence.json", "{\"nextWorkerNum\": 1}");
    }

    private void createFileIfMissing(String filename, String defaultContent) {
        Path path = connection.getFilePath(filename);
        if (!Files.exists(path)) {
            try {
                Files.writeString(path, defaultContent);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + filename, e);
            }
        }
    }

}
