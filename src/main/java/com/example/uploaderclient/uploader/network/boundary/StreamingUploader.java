package com.example.uploaderclient.uploader.network.boundary;

import com.example.uploaderclient.uploader.network.entity.UploadFinishedEvent;
import io.reactivex.Observable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Uploader that transfers data in a streaming manner, i.e. without loading
 * everything into memory before sending a request.
 */
@FunctionalInterface
public interface StreamingUploader {

    /**
     * Returns {@link Observable} that (when subscribed to) sends a request
     * whose {@code OutputStream} is written directly to by provided
     * {@link StreamingResponseBody}. When upload finishes successfully,
     * {@link UploadFinishedEvent} is emitted by provided {@code Observable}.
     * @param dataSource a callback for writing to the request body.
     * @return {@code Observable} that emits {@code UploadFinishedEvent}
     */
    Observable<UploadFinishedEvent> send(StreamingResponseBody dataSource);
}