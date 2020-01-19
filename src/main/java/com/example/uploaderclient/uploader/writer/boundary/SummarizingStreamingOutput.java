/* @(#) $$Id$$
 *
 * Copyright (c) 2000-2020 Comarch SA All Rights Reserved. Any usage,
 * duplication or redistribution of this software is allowed only according to
 * separate agreement prepared in written between Comarch and authorized party.
 */
package com.example.uploaderclient.uploader.writer.boundary;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.uploaderclient.uploader.writer.entity.Statistics;

import io.reactivex.ObservableSource;

/**
 * @author mnowak
 */
public interface SummarizingStreamingOutput extends StreamingResponseBody, ObservableSource<Statistics> {}
