package com.example.uploaderclient.uploader.network.connection.boundary;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.reactivex.Completable;

/**
 * Uploader that transfers data in a streaming manner, i.e. without loading
 * everything into memory before sending a request.
 */
@FunctionalInterface
public interface StreamingUploader {

    /**
     * Returns {@link Completable} that (when subscribed to) sends a request
     * whose {@code OutputStream} is written directly to by provided
     * {@link StreamingResponseBody}.
     * @param dataSource a callback for writing to the request body.
     * @return {@code Completable} representing upload state
     */
    Completable send(StreamingResponseBody dataSource);
}