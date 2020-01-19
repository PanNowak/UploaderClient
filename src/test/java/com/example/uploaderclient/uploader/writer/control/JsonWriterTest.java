package com.example.uploaderclient.uploader.writer.control;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.validation.entity.Error;
import com.example.uploaderclient.uploader.service.control.WriterConfiguration;

import io.vavr.collection.List;

class JsonWriterTest {

    private JsonWriter jsonWriter;

    private Product product;
    private Error error;

    @BeforeEach
    void setUp() {
        WriterConfiguration configuration = new WriterConfiguration();
        jsonWriter = new JsonWriter(configuration.objectMapper());
        product = new Product("testKey", "testType", "testId", Collections.emptyMap());
        error = new Error(null, List.of("firstError", "secondError"));
    }

    @Test
    void shouldCreateOutputStreamWritingJsonThatWillContainAllSuccessfulParsingResults() {
        //dodać jakieś oczekiwane jsony w plikach
//        jsonWriter.write()
    }

}