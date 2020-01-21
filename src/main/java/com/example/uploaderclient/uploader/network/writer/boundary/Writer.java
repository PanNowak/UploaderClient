package com.example.uploaderclient.uploader.network.writer.boundary;

import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;

import io.reactivex.Flowable;

@FunctionalInterface
public interface Writer { //TODO javaDoc

    SummarizingStreamingOutput write(Flowable<ParsingResult> source);
}