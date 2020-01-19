package com.example.uploaderclient.parser.service.control;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.uploaderclient.parser.core.boundary.Parser;
import com.example.uploaderclient.parser.core.boundary.SupportedTypes;
import com.example.uploaderclient.parser.core.entity.FileType;
import com.example.uploaderclient.parser.service.boundary.ParsingService;
import com.example.uploaderclient.parser.validation.control.Validator;
import com.google.common.io.Files;

@SuppressWarnings("UnstableApiUsage")
@Component
public class ParsingServiceFactory {

    private static final String EXCEPTION_MESSAGE_PATTERN =
            "There is no parser capable of reading files with '%s' extension! Supported formats: %s";

    private final EnumMap<FileType, Parser> parsers;
    private final Validator validator;

    @Autowired
    ParsingServiceFactory(List<Parser> parsers, Validator validator) {
        this.parsers = getParserMap(parsers);
        this.validator = validator;
    }

    public Set<FileType> getAllSupportedFileTypes() {
        return parsers.keySet();
    }

    public ParsingService getService(String filename) {
        String fileExtension = Files.getFileExtension(filename);
        return findParser(fileExtension)
                .map(parser -> new DefaultParsingService(parser, validator))
                .orElseThrow(() -> getException(fileExtension));
    }

    private EnumMap<FileType, Parser> getParserMap(List<Parser> parsers) {
        EnumMap<FileType, Parser> parserEnumMap = new EnumMap<>(FileType.class);
        parsers.forEach(parser -> insertToMap(parser, parserEnumMap));
        return parserEnumMap;
    }

    private void insertToMap(Parser parser, EnumMap<FileType, Parser> parserEnumMap) {
        Arrays.stream(parser.getClass().getAnnotations())
                .filter(SupportedTypes.class::isInstance)
                .map(SupportedTypes.class::cast)
                .map(SupportedTypes::value)
                .flatMap(Arrays::stream)
                .forEach(fileType -> parserEnumMap.put(fileType, parser));
    }

    private Optional<Parser> findParser(String fileExtension) {
        return parsers.entrySet().stream()
                .filter(entry -> fileExtension.equals(entry.getKey().getFileExtension()))
                .map(Map.Entry::getValue)
                .findAny();
    }

    private NoSuchElementException getException(String fileExtension) {
        String supportedExtensions = getSupportedExtensions();
        String exceptionMessage = String.format(EXCEPTION_MESSAGE_PATTERN, fileExtension, supportedExtensions);
        return new NoSuchElementException(exceptionMessage);
    }

    private String getSupportedExtensions() {
        return getAllSupportedFileTypes().stream()
                .map(FileType::getFileExtension)
                .collect(Collectors.joining(", "));
    }
}