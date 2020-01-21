package com.example.uploaderclient.uploader.parser.service.control;

import com.example.uploaderclient.uploader.api.boundary.DataSource;
import com.example.uploaderclient.uploader.model.Product;
import com.example.uploaderclient.uploader.parser.core.boundary.Parser;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult.Failure;
import com.example.uploaderclient.uploader.parser.core.entity.ParsingResult.Success;
import com.example.uploaderclient.uploader.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.uploader.parser.validation.control.Validator;
import com.example.uploaderclient.uploader.parser.validation.entity.Error;
import io.reactivex.Flowable;
import io.vavr.collection.List;
import io.vavr.control.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultParsingServiceTest {

    @Mock
    private ProductCandidate productCandidate;

    @Mock
    private DataSource dataSource;

    @Mock
    private Parser parser;

    @Mock
    private Validator validator;

    private Product product;
    private Error error;
    private DefaultParsingService parsingService;

    @BeforeEach
    void setUp() throws IOException {
        parsingService = new DefaultParsingService(parser, validator);
        when(dataSource.openStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        product = new Product("testKey", "testType", "testId", Collections.emptyMap());
        error = new Error(null, List.empty());
    }

    @Test
    void givenThreePositivelyValidatedProductCandidatesShouldReturnThreeSuccessfulParsingResults() {
        Collection<ProductCandidate> productCandidates = Collections.nCopies(3, productCandidate);
        when(parser.read(any(InputStream.class))).thenReturn(Flowable.fromIterable(productCandidates));
        when(validator.validate(any(ProductCandidate.class))).thenReturn(Validation.valid(product));

        ParsingResult expectedResult = new Success(product);
        parsingService.parse(dataSource)
                .test()
                .assertValueCount(3)
                .assertValueAt(0, expectedResult)
                .assertValueAt(1, expectedResult)
                .assertValueAt(2, expectedResult)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    void givenThreeNegativelyValidatedProductCandidatesShouldReturnThreeFailingParsingResults() {
        Collection<ProductCandidate> productCandidates = Collections.nCopies(3, productCandidate);
        when(parser.read(any(InputStream.class))).thenReturn(Flowable.fromIterable(productCandidates));
        when(validator.validate(any(ProductCandidate.class))).thenReturn(Validation.invalid(error));

        ParsingResult expectedResult = new Failure(error);
        parsingService.parse(dataSource)
                .test()
                .assertValueCount(3)
                .assertValueAt(0, expectedResult)
                .assertValueAt(1, expectedResult)
                .assertValueAt(2, expectedResult)
                .assertNoErrors()
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void givenDiverseProductCandidatesShouldReturnRelevantResult() {
        Collection<ProductCandidate> productCandidates = Collections.nCopies(3, productCandidate);
        when(parser.read(any(InputStream.class))).thenReturn(Flowable.fromIterable(productCandidates));
        when(validator.validate(any(ProductCandidate.class)))
                .thenReturn(Validation.invalid(error), Validation.valid(product), Validation.invalid(error));

        parsingService.parse(dataSource)
                .test()
                .assertValueCount(3)
                .assertValueAt(0, new Failure(error))
                .assertValueAt(1, new Success(product))
                .assertValueAt(2, new Failure(error))
                .assertNoErrors()
                .assertComplete();
    }
}