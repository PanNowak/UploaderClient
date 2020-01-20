package com.example.uploaderclient.uploader.service.control;

import com.example.uploaderclient.api.entity.Statistics;
import com.example.uploaderclient.parser.core.entity.ParsingResult;
import com.example.uploaderclient.uploader.network.boundary.StreamingUploader;
import com.example.uploaderclient.uploader.service.boundary.UploadingService;
import com.example.uploaderclient.uploader.writer.boundary.SummarizingStreamingOutput;
import com.example.uploaderclient.uploader.writer.boundary.Writer;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Observable<Statistics> statisticsObservable = getStatisticsObservable(summarizingStreamingOutput);
        Observable<Statistics> responseObservable = getResponseObservable(summarizingStreamingOutput);

        return Observable.mergeDelayError(statisticsObservable, responseObservable);
    }

    private Observable<Statistics> getStatisticsObservable(ObservableSource<Statistics> source) {
        return Observable.unsafeCreate(source);
    }

    private Observable<Statistics> getResponseObservable(SummarizingStreamingOutput streamingOutput) {
        return uploader.send(streamingOutput)
                .doOnError(error -> streamingOutput.ensureOnComplete())
                .toObservable();
    }
}