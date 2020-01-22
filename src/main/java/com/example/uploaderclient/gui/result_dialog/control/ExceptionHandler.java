package com.example.uploaderclient.gui.result_dialog.control;

import com.example.uploaderclient.uploader.parser.core.entity.ParsingException;
import io.reactivex.exceptions.CompositeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ExceptionHandler { //TODO lepiej zwracać klasę z wiadomościami i pozwolić okienku samemu sformatować

    private static final String COMPOSITE_EXCEPTION_HEADER = "%s problems appeared:";
    private static final String PARSING_EXCEPTION_MESSAGE_PATTERN = "Upload was interrupted by parsing failure " +
            "in '%s'. Some data may have been sent.";
    private static final String RESPONSE_EXCEPTION_MESSAGE_PATTERN = "Upload ended but server returned response " +
            "with an error -> code: %s, message: %s";
    private static final String UNEXPECTED_EXCEPTION_MESSAGE_PATTERN = "Unknown exception occurred during upload " +
            "-> type: %s, message: %s";

    public String logExceptionAndReturnErrorMessage(Throwable exception) {
        log.error("Exception occurred during upload:", exception);

        String message;
        if (exception instanceof CompositeException) {
            message = handleCompositeException((CompositeException) exception);
        } else {
            message = handleSingularException(exception);
        }

        return message + "\nPlease check logs for details.";
    }

    private String handleCompositeException(CompositeException exception) {
        Set<String> exceptionMessages = getDistinctExceptionMessages(exception.getExceptions());
        String header = String.format(COMPOSITE_EXCEPTION_HEADER, exceptionMessages.size());

        StringBuilder messageBuilder = new StringBuilder(header);
        for (String messagePart : exceptionMessages) {
            messageBuilder.append("\n\t- ").append(messagePart);
        }

        return messageBuilder.toString();
    }

    private Set<String> getDistinctExceptionMessages(List<Throwable> allExceptions) {
        return allExceptions.stream()
                .map(this::handleSingularException)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String handleSingularException(Throwable exception) {
        if (exception instanceof ParsingException) {
            return handleParsingException((ParsingException) exception);
        } else if (exception instanceof ResponseStatusException) {
            return handleResponseStatusException((ResponseStatusException) exception);
        } else if (exception instanceof ResourceAccessException) {
            return "Could not connect to the server";
        } else {
            return handleUnexpectedException(exception);
        }
    }

    private String handleParsingException(ParsingException exception) {
        return String.format(PARSING_EXCEPTION_MESSAGE_PATTERN, exception.getSourceName());
    }

    private String handleResponseStatusException(ResponseStatusException exception) {
        HttpStatus httpStatus = exception.getStatus();
        return String.format(RESPONSE_EXCEPTION_MESSAGE_PATTERN, httpStatus.value(), httpStatus.getReasonPhrase());
    }

    private String handleUnexpectedException(Throwable exception) {
        Throwable originalCause = getOriginalCause(exception);
        return String.format(UNEXPECTED_EXCEPTION_MESSAGE_PATTERN,
                originalCause.getClass().getSimpleName(), originalCause.getMessage());
    }

    private Throwable getOriginalCause(Throwable exception) {
        Throwable original = exception;
        while (original.getCause() != null) {
            original = original.getCause();
        }

        return original;
    }
}