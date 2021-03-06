package com.example.uploaderclient.uploader.parser.service.control;

import com.example.uploaderclient.uploader.api.boundary.DataSource;
import com.example.uploaderclient.uploader.model.Product;
import com.example.uploaderclient.uploader.parser.core.boundary.Parser;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingException;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult.Failure;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult.Success;
import com.example.uploaderclient.uploader.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.uploader.parser.service.boundary.ParsingService;
import com.example.uploaderclient.uploader.parser.validation.control.Validator;
import com.example.uploaderclient.uploader.parser.validation.entity.Error;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Function;
import io.vavr.control.Validation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class DefaultParsingService implements ParsingService {

    private final Parser parser;
    private final Validator validator;

    DefaultParsingService(Parser parser, Validator validator) {
        this.parser = parser;
        this.validator = validator;
    }

    @Override
    public Flowable<ParsingResult> parse(DataSource dataSource) {
        return Flowable.fromCallable(dataSource::openStream)
                .concatMap(parser::read)
                .compose(addParsingInfo(dataSource))
                .map(validator::validate)
                .map(this::getParsingResult);
    }

    private FlowableTransformer<ProductCandidate, ProductCandidate> addParsingInfo(DataSource dataSource) {
        String fullDataSourceName = dataSource.getName();
        return productCandidates -> productCandidates
                .doOnSubscribe(subscription -> log.info("Parsing of data from '{}' started.", fullDataSourceName))
                .doOnComplete(() -> log.info("Parsing of data from '{}' ended.", fullDataSourceName))
                .doOnCancel(() -> log.info("Parsing of data from '{}' was canceled.", fullDataSourceName))
                .onErrorResumeNext(exceptionWithSourceNameMapper(dataSource));
    }

    private Function<Throwable, Flowable<ProductCandidate>> exceptionWithSourceNameMapper(DataSource dataSource) {
        return originalException -> Flowable.error(
                new ParsingException(dataSource.getName(), dataSource.getShortName(),  originalException));
    }

    private ParsingResult getParsingResult(Validation<Error, Product> productValidation) {
        if (productValidation.isValid()) {
            return new Success(productValidation.get());
        }

        Error validationError = productValidation.getError();
        log.warn(validationError.getErrorMessage());
        return new Failure(validationError);
    }
}