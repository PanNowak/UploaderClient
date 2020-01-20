package com.example.uploaderclient.uploader.writer.control;

import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.core.entity.ParsingResult;
import com.example.uploaderclient.parser.core.entity.ParsingResult.Success;
import com.example.uploaderclient.parser.validation.entity.Error;
import com.example.uploaderclient.uploader.service.control.WriterConfiguration;
import com.example.uploaderclient.uploader.writer.boundary.SummarizingStreamingOutput;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Flowable;
import io.vavr.collection.List;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonWriterTest {

    private static final String EXPECTED_JSON = "[ {\n" +
            "  \"key\" : \"testKey\",\n" +
            "  \"type\" : \"testType\",\n" +
            "  \"identifier\" : \"testId\",\n" +
            "  \"simpleAttributes\" : {\n" +
            "    \"testKey\" : \"testValue\"\n" +
            "  }\n" +
            "}, {\n" +
            "  \"key\" : \"testKey\",\n" +
            "  \"type\" : \"testType\",\n" +
            "  \"identifier\" : \"testId\",\n" +
            "  \"simpleAttributes\" : {\n" +
            "    \"testKey\" : \"testValue\"\n" +
            "  }\n" +
            "}, {\n" +
            "  \"key\" : \"testKey\",\n" +
            "  \"type\" : \"testType\",\n" +
            "  \"identifier\" : \"testId\",\n" +
            "  \"simpleAttributes\" : {\n" +
            "    \"testKey\" : \"testValue\"\n" +
            "  }\n" +
            "} ]";

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
    void shouldCreateJsonThatWillContainAllSuccessfulParsingResults() throws IOException {
        Flowable<ParsingResult> flowable = Flowable.just(new Success(product))
                .repeat(3).cast(ParsingResult.class);
        SummarizingStreamingOutput streamingOutput = jsonWriter.write(flowable);

        StringWriter stringWriter = new StringWriter();
        streamingOutput.writeTo(new WriterOutputStream(stringWriter, Charset.defaultCharset()));

        assertEquals(EXPECTED_JSON, stringWriter.toString());

    }

}