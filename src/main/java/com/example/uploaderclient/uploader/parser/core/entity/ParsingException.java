package com.example.uploaderclient.uploader.parser.core.entity;

import java.io.IOException;

public class ParsingException extends IOException {

    private static final String ERROR_MESSAGE_PATTERN = "Exception occurred during parsing data from '%s'.";

    private final String sourceName;

    public ParsingException(String sourceName, Throwable cause) {
        super(String.format(ERROR_MESSAGE_PATTERN, sourceName), cause);
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}