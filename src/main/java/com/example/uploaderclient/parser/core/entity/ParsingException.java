/* @(#) $$Id$$
 *
 * Copyright (c) 2000-2020 Comarch SA All Rights Reserved. Any usage,
 * duplication or redistribution of this software is allowed only according to
 * separate agreement prepared in written between Comarch and authorized party.
 */
package com.example.uploaderclient.parser.core.entity;

import java.io.IOException;

/**
 * @author mnowak
 */
public class ParsingException extends IOException {

    private static final String ERROR_MESSAGE_PATTERN = "Exception occurred during parsing of '%s'.";

    private final String sourceName;

    public ParsingException(String sourceName, Throwable cause) {
        super(String.format(ERROR_MESSAGE_PATTERN, sourceName), cause);
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}