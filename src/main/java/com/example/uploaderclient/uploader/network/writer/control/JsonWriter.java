package com.example.uploaderclient.uploader.network.writer.control;

import com.example.uploaderclient.uploader.api.entity.Statistics;
import com.example.uploaderclient.uploader.network.service.entity.RequestWritingException;
import com.example.uploaderclient.uploader.network.writer.boundary.SummarizingStreamingOutput;
import com.example.uploaderclient.uploader.network.writer.boundary.Writer;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SafeSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

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

        private JsonGenerator createJsonGenerator(OutputStream sink) throws IOException {
            return objectMapper.getFactory()
                    .createGenerator(sink)
                    .useDefaultPrettyPrinter();
        }

        private void writeAllProducts(JsonGenerator generator) {
            Subscriber<ParsingResult> writingSubscriber = new WritingSubscriber(publishSubject, generator);
            source.repeat(3000).blockingSubscribe(new SafeSubscriber<>(writingSubscriber));
        }

        @Override
        public void subscribe(Observer<? super Statistics> observer) {
            publishSubject.scan(Statistics.empty(), Statistics::combine)
                    .throttleLatest(100, TimeUnit.MILLISECONDS, true)
                    .subscribe(observer);
        }

        @Override
        public void ensureOnComplete() {
            publishSubject.onComplete();
        }
    }

    private final class WritingSubscriber extends DisposableSubscriber<ParsingResult> {

        private final PublishSubject<Statistics> publishSubject;
        private final JsonGenerator generator;

        WritingSubscriber(PublishSubject<Statistics> publishSubject, JsonGenerator generator) {
            this.publishSubject = publishSubject;
            this.generator = generator;
        }

        @Override
        public void onStart() {
            request(1);
        }

        @Override
        public void onNext(ParsingResult parsingResult) {
            try {
                writeProductIfSuccess(parsingResult);
                requestMoreIfNeededOrCancel();
            } catch (IOException e) {
                RequestWritingException requestWritingException =
                        new RequestWritingException("Exception occurred during writing to request body", e);
                ExceptionUtils.rethrow(requestWritingException);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            publishSubject.onError(throwable);
        }

        @Override
        public void onComplete() {
            publishSubject.onComplete();
        }

        private void writeProductIfSuccess(ParsingResult parsingResult) throws IOException {
            Statistics statistics;
            if (parsingResult.isSuccessful()) {
                objectMapper.writeValue(generator, parsingResult.getProduct());

//
//                System.out.println("Writing:" + parsingResult.getProduct() + " on " + Thread.currentThread().getName());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                statistics = Statistics.success();
            } else {
                statistics = Statistics.error(parsingResult.getError().getErrorCount());
            }

            publishSubject.onNext(statistics);
        }

        private void requestMoreIfNeededOrCancel() {
            if (publishSubject.hasObservers()) {
                request(1);
            } else {
                cancel();
            }
        }
    }
}