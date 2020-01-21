package com.example.uploaderclient.uploader.parser.service.boundary;

import com.example.uploaderclient.uploader.api.boundary.DataSource;
import com.example.uploaderclient.uploader.model.Product;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import io.reactivex.Flowable;

/**
 * Simple service that combines parser and validator.
 */
@FunctionalInterface
public interface ParsingService {

    /**
     * Returns {@link Flowable} of positively or negatively validated {@link Product}s,
     * each represented by a {@link ParsingResult}.
     * @param dataSource source of data, <b>must not be null</b>
     * @return {@code Flowable} of {@code ParsingResult}s
     */
    Flowable<ParsingResult> parse(DataSource dataSource);
}