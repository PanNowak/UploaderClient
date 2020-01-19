package com.example.uploaderclient.parser.validation.entity;

import com.example.uploaderclient.parser.core.entity.ProductCandidate;

import io.vavr.collection.Seq;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Error {

    private static final String ERROR_MESSAGE_PATTERN =
            "Following validation errors occurred during parsing of %s:";
    private static final String ERRORS_DELIMITER = "%n - ";

    private final String errorMessage;
    private final int errorCount;

    public Error(ProductCandidate erroneousObject, Seq<String> errors) {
        this.errorMessage = createErrorMessage(erroneousObject, errors);
        this.errorCount = errors.size();
    }

    private String createErrorMessage(ProductCandidate erroneousObject, Seq<String> errors) {
        String concatenatedErrors = errors.mkString(ERRORS_DELIMITER, ERRORS_DELIMITER, "");
        return String.format(ERROR_MESSAGE_PATTERN + concatenatedErrors, erroneousObject);
    }
}