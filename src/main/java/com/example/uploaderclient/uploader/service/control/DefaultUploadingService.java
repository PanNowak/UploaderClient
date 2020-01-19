package com.example.uploaderclient.uploader.service.control;

import com.example.uploaderclient.api.boundary.UploadInfo;
import com.example.uploaderclient.parser.core.entity.ParsingResult;
import com.example.uploaderclient.uploader.network.boundary.StreamingUploader;
import com.example.uploaderclient.uploader.service.boundary.UploadingService;
import com.example.uploaderclient.uploader.network.entity.UploadFinishedEvent;
import com.example.uploaderclient.uploader.writer.boundary.SummarizingStreamingOutput;
import com.example.uploaderclient.uploader.writer.boundary.Writer;
import io.reactivex.Flowable;
import io.reactivex.Observable;
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
    public Observable<UploadInfo> upload(Flowable<ParsingResult> source) {
        SummarizingStreamingOutput summarizingStreamingOutput = dataWriter.write(source);
        Observable<UploadFinishedEvent> response = uploader.send(summarizingStreamingOutput);
        return Observable.mergeDelayError(summarizingStreamingOutput, response);
    }
}