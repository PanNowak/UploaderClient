package com.example.uploaderclient.uploader.network.writer.control;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.uploaderclient.uploader.api.entity.Statistics;
import com.example.uploaderclient.uploader.model.Product;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult.Failure;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult.Success;
import com.example.uploaderclient.uploader.parser.validation.entity.Error;
import com.example.uploaderclient.uploader.network.service.control.WriterConfiguration;
import com.example.uploaderclient.uploader.network.writer.boundary.SummarizingStreamingOutput;
import com.google.common.collect.ImmutableMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.vavr.collection.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonWriterTest {

    private JsonWriter jsonWriter;

    private Product product;
    private Error error;

    @BeforeEach
    void setUp() {
        WriterConfiguration configuration = new WriterConfiguration();
        jsonWriter = new JsonWriter(configuration.objectMapper());
        product = new Product("testKey", "testType", "testId",
                ImmutableMap.of("testKey", "testValue"));
        error = new Error(null, List.of("firstError", "secondError"));
    }

    @Test
    void shouldCreateJsonAndEmitStatisticsAboutSuccessfulParsingResultsOnly() throws IOException {
        SummarizingStreamingOutput streamingOutput = Flowable.just(new Success(product))
                .repeat(3).cast(ParsingResult.class).to(jsonWriter::write);

        LinkedList<Statistics> statistics = getListOfEmittedStatistics(streamingOutput);
        String json = writeJsonAndReturnItAsString(streamingOutput);

        assertEquals(getExpectedCorrectJson(), json);
        assertEquals(new Statistics(3, 0, 0), statistics.getLast());
    }

    @Test
    void shouldCreateEmptyJsonAndEmitStatisticsAboutFailedParsingResultsOnly() throws IOException {
        SummarizingStreamingOutput streamingOutput = Flowable.just(new Failure(error))
                .repeat(3).cast(ParsingResult.class).to(jsonWriter::write);

        LinkedList<Statistics> statistics = getListOfEmittedStatistics(streamingOutput);
        String json = writeJsonAndReturnItAsString(streamingOutput);

        assertEquals("[ ]", json);
        assertEquals(new Statistics(0, 3, 6), statistics.getLast());
    }

    @Test
    void shouldCreateJsonAndEmitRelevantStatistics() throws IOException {
        SummarizingStreamingOutput streamingOutput =
                Flowable.just(new Success(product), new Failure(error), new Success(product), new Success(product))
                        .cast(ParsingResult.class).to(jsonWriter::write);

        LinkedList<Statistics> statistics = getListOfEmittedStatistics(streamingOutput);
        String json = writeJsonAndReturnItAsString(streamingOutput);

        assertEquals(getExpectedCorrectJson(), json);
        assertEquals(new Statistics(3, 1, 2), statistics.getLast());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private LinkedList<Statistics> getListOfEmittedStatistics(SummarizingStreamingOutput streamingOutput) {
        LinkedList<Statistics> statistics = new LinkedList<>();
        Observable.unsafeCreate(streamingOutput).subscribe(statistics::add);
        return statistics;
    }

    private String writeJsonAndReturnItAsString(SummarizingStreamingOutput streamingOutput) throws IOException {
        StringWriter stringWriter = new StringWriter();
        streamingOutput.writeTo(new WriterOutputStream(stringWriter, Charset.defaultCharset()));
        return stringWriter.toString();
    }

    private String getExpectedCorrectJson() throws IOException {
        return IOUtils.resourceToString("Expected-write-result.json",
                Charset.defaultCharset(), getClass().getClassLoader());
    }
}