package com.example.uploaderclient.uploader.network.service.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.uploaderclient.uploader.api.entity.Statistics;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import com.example.uploaderclient.uploader.network.connection.boundary.StreamingUploader;
import com.example.uploaderclient.uploader.network.service.boundary.UploadingService;
import com.example.uploaderclient.uploader.network.writer.boundary.SummarizingStreamingOutput;
import com.example.uploaderclient.uploader.network.writer.boundary.Writer;

import io.reactivex.Flowable;
import io.reactivex.Observable;

@Service
class DefaultUploadingService implements UploadingService {

    private final Writer dataWriter;
    private final StreamingUploader uploader;

    @Autowired
    DefaultUploadingService(Writer dataWriter, StreamingUploader uploader) {
        this.dataWriter = dataWriter;
        this.uploader = uploader;
    }

    @Override
    public Observable<Statistics> upload(Flowable<ParsingResult> source) {
        SummarizingStreamingOutput summarizingStreamingOutput = dataWriter.write(source);
        Observable<Statistics> responseObservable = getResponseObservable(summarizingStreamingOutput);

        return Observable.mergeDelayError(summarizingStreamingOutput, responseObservable);
    }

    private Observable<Statistics> getResponseObservable(SummarizingStreamingOutput streamingOutput) {
        return uploader.send(streamingOutput)
                .doOnError(error -> streamingOutput.ensureOnComplete())
                .toObservable();
    }
}