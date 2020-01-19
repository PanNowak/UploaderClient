package com.example.uploaderclient.parser.core.boundary;

import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.core.entity.ProductCandidate;
import io.reactivex.Flowable;
import io.reactivex.exceptions.MissingBackpressureException;

import java.io.InputStream;

/**
 * Specifies parser that reads data representing {@link Product}s from an arbitrary
 * {@link InputStream}. Implementations should strive to work in a streaming manner,
 * i.e. not loading all data into memory at once. Classes implementing this interface
 * are encouraged <b>not</b> to check whether created {@link ProductCandidate}s
 * contain all necessary infromation since validation is to be performed
 * in subsequent processing stages.
 */
@FunctionalInterface
public interface Parser {

    /**
     * Reads data from the provided {@code InputStream} and returns {@code Flowable}
     * of {@code ProductCandidate}s. Since backing data is either a lazy stream
     * or a collection, returned {@code Flowable} can and must provide full backpressure
     * support. Failure to do so may result in {@link MissingBackpressureException}
     * being thrown.
     * @param dataSource {@code InputStream} to read data from, <b>must not be null</b>
     * @return {@code Flowable} of {@code ProductCandidate}s
     */
    Flowable<ProductCandidate> read(InputStream dataSource);
}