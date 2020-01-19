package com.example.uploaderclient.uploader.writer.entity;

import com.example.uploaderclient.api.boundary.UploadInfo;
import com.google.common.collect.ImmutableMap;
import lombok.Value;

import java.util.Map;

@Value
public final class Statistics implements UploadInfo {

    public static Statistics empty() {
        return new Statistics(0, 0, 0);
    }

    public static Statistics success() {
        return new Statistics(1, 0, 0);
    }

    public static Statistics error(int totalNumberOfProblems) {
        return new Statistics(0, 1, totalNumberOfProblems);
    }

    private final int numberOfSuccessfullyParsedObjects;
    private final int numberOfParsingFailures;
    private final int totalNumberOfProblems;

    private Statistics(int numberOfSuccessfullyParsedObjects, int numberOfParsingFailures,
                       int totalNumberOfProblems) {
        this.numberOfSuccessfullyParsedObjects = numberOfSuccessfullyParsedObjects;
        this.numberOfParsingFailures = numberOfParsingFailures;
        this.totalNumberOfProblems = totalNumberOfProblems;
    }

    public Statistics combine(Statistics other) {
        int numberOfSuccessfullyParsedObjects = this.numberOfSuccessfullyParsedObjects +
                other.numberOfSuccessfullyParsedObjects;
        int numberOfParsingFailures = this.numberOfParsingFailures + other.numberOfParsingFailures;
        int totalNumberOfProblems = this.totalNumberOfProblems + other.totalNumberOfProblems;

        return new Statistics(numberOfSuccessfullyParsedObjects,
                numberOfParsingFailures, totalNumberOfProblems);
    }

    @Override
    public String header() {
        return "Current statistics";
    }

    @Override
    public Map<String, String> body() {
        return ImmutableMap.of(
                "Number of successfully parsed objects", String.valueOf(numberOfSuccessfullyParsedObjects),
                "Number of parsing failures", String.valueOf(numberOfParsingFailures),
                "Total number of problems", String.valueOf(totalNumberOfProblems));
    }
}