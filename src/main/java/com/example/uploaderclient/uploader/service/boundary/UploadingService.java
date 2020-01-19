package com.example.uploaderclient.uploader.service.boundary;

import com.example.uploaderclient.api.boundary.UploadInfo;
import com.example.uploaderclient.parser.core.entity.ParsingResult;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Service that uploads provided {@link ParsingResult}s and emits information about progress.
 */
@FunctionalInterface
public interface UploadingService {

    /**
     * Returns {@link Observable} that upon subscription reads data from the given
     * {@link Flowable}, sends them streamingly to the desired destination and
     * periodically emits {@link UploadInfo} with information about current upload state.
     * @param source backpressured source of {@code ParsingResult}s
     * @return {@code Observable} emitting current upload state
     */
    Observable<UploadInfo> upload(Flowable<ParsingResult> source);
}