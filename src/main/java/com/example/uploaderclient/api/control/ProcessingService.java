/* @(#) $$Id$$
 *
 * Copyright (c) 2000-2020 Comarch SA All Rights Reserved. Any usage,
 * duplication or redistribution of this software is allowed only according to
 * separate agreement prepared in written between Comarch and authorized party.
 */
package com.example.uploaderclient.api.control;

import com.example.uploaderclient.api.boundary.DataSource;
import com.example.uploaderclient.api.boundary.UploadInfo;
import com.example.uploaderclient.parser.core.entity.FileType;
import com.example.uploaderclient.parser.core.entity.ParsingResult;
import com.example.uploaderclient.parser.service.control.ParsingServiceFactory;
import com.example.uploaderclient.uploader.service.boundary.UploadingService;
import com.google.common.io.Files;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author mnowak
 */
@Service
public class ProcessingService {

    private final ParsingServiceFactory parsingServiceFactory;
    private final UploadingService uploadingService;

    @Autowired
    ProcessingService(ParsingServiceFactory parsingServiceFactory, UploadingService uploadingService) {
        this.parsingServiceFactory = parsingServiceFactory;
        this.uploadingService = uploadingService;
    }

    public Set<FileType> getAllSupportedFileTypes() {
        return parsingServiceFactory.getAllSupportedFileTypes();
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean isSupportedFileType(String filename) {
        String fileExtension = Files.getFileExtension(filename);
        return getAllSupportedFileTypes().stream()
                .map(FileType::getFileExtension)
                .anyMatch(fileExtension::equals);
    }

    public Observable<UploadInfo> parseAndUpload(DataSource... dataSources) {
        return parseAndUpload(Arrays.asList(dataSources));
    }

    public Observable<UploadInfo> parseAndUpload(List<? extends DataSource> dataSources) {
        return Flowable.fromIterable(dataSources)
                .concatMap(this::parse)
                .to(uploadingService::upload);
    }

    private Flowable<ParsingResult> parse(DataSource dataSource) {
        return parsingServiceFactory.getService(dataSource.getName()).parse(dataSource);
    }
}