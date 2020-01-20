package com.example.uploaderclient.api.control;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.uploaderclient.api.boundary.DataSource;

import lombok.ToString;

@ToString
public final class FileBasedSource implements DataSource {

    private static final String FILE_NOT_EXISTS_ERROR_MESSAGE_PATTERN = "File '%s' does not exist!'";
    private static final String NOT_A_FILE_ERROR_MESSAGE_PATTERN = "Path '%s' does not point to a file!";

    private final Path pathToFile;

    public FileBasedSource(Path pathToFile) {
        this.pathToFile = validateFileExists(pathToFile);
    }

    @Override
    public String getName() {
        return pathToFile.toString();
    }

    @Override
    public String getShortName() {
        return pathToFile.getFileName().toString();
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(pathToFile);
    }

    private Path validateFileExists(Path pathToFile) {
        if (!Files.exists(pathToFile)) {
            String errorMessage = String.format(FILE_NOT_EXISTS_ERROR_MESSAGE_PATTERN, pathToFile);
            throw new IllegalStateException(errorMessage);
        } else if (!Files.isRegularFile(pathToFile)) {
            String errorMessage = String.format(NOT_A_FILE_ERROR_MESSAGE_PATTERN, pathToFile);
            throw new IllegalStateException(errorMessage);
        }

        return pathToFile;
    }
}