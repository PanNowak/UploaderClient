package com.example.uploaderclient.parser.service.control;

import com.example.uploaderclient.api.boundary.DataSource;
import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.core.boundary.Parser;
import com.example.uploaderclient.parser.core.entity.ParsingException;
import com.example.uploaderclient.parser.core.entity.ParsingResult;
import com.example.uploaderclient.parser.core.entity.ParsingResult.Failure;
import com.example.uploaderclient.parser.core.entity.ParsingResult.Success;
import com.example.uploaderclient.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.parser.service.boundary.ParsingService;
import com.example.uploaderclient.parser.validation.control.Validator;
import com.example.uploaderclient.parser.validation.entity.Error;

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
//                .doOnNext(p ->  System.out.println("Reading:" + p + " on " + Thread.currentThread().getName()))
                .compose(addParsingInfo(dataSource.getName()))
                .map(validator::validate)
                .map(this::getParsingResult);
    }

    private FlowableTransformer<ProductCandidate, ProductCandidate> addParsingInfo(String sourceName) {
        return productCandidates -> productCandidates
                .doOnSubscribe(subscription -> log.info("Parsing of '{}' started.", sourceName))
                .doOnComplete(() -> log.info("Parsing of '{}' ended.", sourceName))
                .onErrorResumeNext(exceptionWithSourceNameMapper(sourceName));
    }

    private Function<Throwable, Flowable<ProductCandidate>> exceptionWithSourceNameMapper(String sourceName) {
        return originalException -> Flowable.error(new ParsingException(sourceName, originalException));
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