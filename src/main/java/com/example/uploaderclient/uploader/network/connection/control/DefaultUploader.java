package com.example.uploaderclient.uploader.network.connection.control;

import com.example.uploaderclient.uploader.network.connection.boundary.StreamingUploader;
import io.reactivex.Completable;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    public Completable send(StreamingResponseBody dataSource) {
        return Single.fromCallable(() -> prepareRequestCallback(dataSource))
                .flatMapCompletable(this::sendRequestAndAwaitFinishedEvent);
    }

    private RequestCallback prepareRequestCallback(StreamingResponseBody dataSource) {
        return request -> {
            request.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            dataSource.writeTo(request.getBody());
        };
    }

    private Completable sendRequestAndAwaitFinishedEvent(RequestCallback requestCallback) {
        return restTemplate.execute(uploadUri, HttpMethod.POST, requestCallback, this::processResponse);
    }

    private Completable processResponse(ClientHttpResponse response) throws IOException {
        try (ClientHttpResponse r = response) {
            HttpStatus statusCode = r.getStatusCode();
            String statusText = r.getStatusText();

            if (statusCode.isError()) {
                return Completable.error(new ResponseStatusException(statusCode, statusText));
            }

            log.info("Received response: {}", statusCode); //TODO pewnie jakiś filtr odpowiedzi
            return Completable.complete();
        }
    }
}