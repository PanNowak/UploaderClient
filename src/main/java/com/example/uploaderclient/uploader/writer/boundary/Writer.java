package com.example.uploaderclient.uploader.writer.boundary;

import com.example.uploaderclient.parser.core.entity.ParsingResult;

import io.reactivex.Flowable;

@FunctionalInterface
public interface Writer { //TODO javaDoc

    SummarizingStreamingOutput write(Flowable<ParsingResult> source);
}