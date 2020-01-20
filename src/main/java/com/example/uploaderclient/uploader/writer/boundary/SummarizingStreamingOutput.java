package com.example.uploaderclient.uploader.writer.boundary;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.uploaderclient.api.entity.Statistics;

import io.reactivex.ObservableSource;

public interface SummarizingStreamingOutput extends StreamingResponseBody, ObservableSource<Statistics> {

    void ensureOnComplete();
}