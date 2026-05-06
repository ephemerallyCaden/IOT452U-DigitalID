package com.digitalid.infrastructure.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resolves file paths for the JSON flat-file persistence layer.
 * Manages the data directory where all JSON data files are stored.
 */
public class DataStorePath {

    private final Path dataDirectory;

    public DataStorePath(String directoryPath) {
        this.dataDirectory = Paths.get(directoryPath);
        initialise();
    }

    public DataStorePath() {
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
