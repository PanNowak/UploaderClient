package com.example.uploaderclient.uploader.network.writer.control;

import com.example.uploaderclient.uploader.api.entity.Statistics;
import com.example.uploaderclient.uploader.network.writer.boundary.SummarizingStreamingOutput;
import com.example.uploaderclient.uploader.network.writer.boundary.Writer;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
public class JsonWriter implements Writer {

    private final ObjectMapper objectMapper;

    @Autowired
    JsonWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SummarizingStreamingOutput write(Flowable<ParsingResult> source) {
        return new JsonSummarizingStreamingOutput(source);
    }

    private final class JsonSummarizingStreamingOutput implements SummarizingStreamingOutput {

        private final Flowable<ParsingResult> source;
        private final PublishSubject<Statistics> publishSubject;

        JsonSummarizingStreamingOutput(Flowable<ParsingResult> source) {
            this.source = source;
            this.publishSubject = PublishSubject.create();
        }

        @Override
        public void writeTo(@NonNull OutputStream sink) throws IOException {
            try (JsonGenerator generator = createJsonGenerator(sink)) {
                log.info("Writing data started.");

                generator.writeStartArray();
                writeAllProducts(generator);
                generator.writeEndArray();

                log.info("Writing data ended.");
            }
        }

        @Override
        public void subscribe(Observer<? super Statistics> observer) {
            publishSubject.scan(Statistics.empty(), Statistics::combine)
                    .subscribe(observer);
        }

        @Override
        public void ensureOnComplete() {
            publishSubject.onComplete();
        }

        private JsonGenerator createJsonGenerator(OutputStream sink) throws IOException {
            return objectMapper.getFactory()
                    .createGenerator(sink)
                    .useDefaultPrettyPrinter();
        }

        private void writeAllProducts(JsonGenerator generator) {
            source.blockingSubscribe(parsingResult -> writeProductIfSuccess(generator, parsingResult),
                    publishSubject::onError, publishSubject::onComplete, 1);
        }

        private void writeProductIfSuccess(JsonGenerator generator, ParsingResult parsingResult) throws IOException {
            Statistics statistics;
            if (parsingResult.isSuccessful()) {
                objectMapper.writeValue(generator, parsingResult.getProduct());
                statistics = Statistics.success();
            } else {
                statistics = Statistics.error(parsingResult.getError().getErrorCount());
            }

            publishSubject.onNext(statistics);
        }
    }
}