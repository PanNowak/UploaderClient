package com.example.uploaderclient.uploader.network.writer.boundary;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.uploaderclient.uploader.api.entity.Statistics;

import io.reactivex.ObservableSource;

public interface SummarizingStreamingOutput extends StreamingResponseBody, ObservableSource<Statistics> {

    void ensureOnComplete();
}