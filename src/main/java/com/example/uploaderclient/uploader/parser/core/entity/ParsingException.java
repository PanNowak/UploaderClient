package com.example.uploaderclient.uploader.parser.core.entity;

import java.io.IOException;

public class ParsingException extends IOException {

    private static final String ERROR_MESSAGE_PATTERN = "Exception occurred during parsing data from '%s'.";

    private final String sourceShortName;

    public ParsingException(String sourceFullName, String sourceShortName, Throwable cause) {
        super(String.format(ERROR_MESSAGE_PATTERN, sourceFullName), cause);
        this.sourceShortName = sourceShortName;
    }

    public String getSourceName() {
        return sourceShortName;
    }
}