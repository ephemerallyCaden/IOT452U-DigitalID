package com.digitalid.infrastructure.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseConnection {

    private final Path dataDirectory;

    public DatabaseConnection(String directoryPath) {
        this.dataDirectory = Paths.get(directoryPath);
        initialise();
    }

    public DatabaseConnection() {
        this("data");
    }

    private void initialise() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory: " + dataDirectory, e);
        }
    }

    public Path getFilePath(String filename) {
        return dataDirectory.resolve(filename);
    }

    public boolean fileExists(String filename) {
        return Files.exists(getFilePath(filename));
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

}
