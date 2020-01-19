package com.example.uploaderclient.uploader.network.control;

import com.example.uploaderclient.uploader.network.boundary.StreamingUploader;
import com.example.uploaderclient.uploader.network.entity.UploadFinishedEvent;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@Component
public class DefaultUploader implements StreamingUploader {

    private final RestTemplate restTemplate;
    private final String uploadUri;

    @Autowired
    DefaultUploader(RestTemplate restTemplate, @Value("${upload.uri}") String uploadUri) {
        this.restTemplate = restTemplate;
        this.uploadUri = uploadUri;
    }

    @Override
    public Observable<UploadFinishedEvent> send(StreamingResponseBody dataSource) {
        return Observable.fromCallable(() -> prepareRequestCallback(dataSource))
                .flatMap(this::sendRequestAndAwaitFinishedEvent);
    }

    private RequestCallback prepareRequestCallback(StreamingResponseBody dataSource) {
        return request -> {
            request.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            dataSource.writeTo(request.getBody());
        };
    }

    private Observable<UploadFinishedEvent> sendRequestAndAwaitFinishedEvent(RequestCallback requestCallback) {
        return restTemplate.execute(uploadUri, HttpMethod.POST, requestCallback, this::processResponse);
    }

    private Observable<UploadFinishedEvent> processResponse(ClientHttpResponse response) throws IOException {
        try (ClientHttpResponse r = response) {
            HttpStatus statusCode = r.getStatusCode();
            String statusText = r.getStatusText();

            if (statusCode.isError()) {
                return Observable.error(new ResponseStatusException(statusCode, statusText));
            }

            String statusCodeValue = String.valueOf(statusCode.value());
            return Observable.just(new UploadFinishedEvent(statusCodeValue, statusText));
        }
    }
}