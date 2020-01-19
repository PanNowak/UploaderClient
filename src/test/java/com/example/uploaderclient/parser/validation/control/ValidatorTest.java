package com.example.uploaderclient.parser.validation.control;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.parser.validation.entity.Error;
import com.google.common.collect.ImmutableMap;

import io.vavr.control.Validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorTest {

    private static final String TEST_KEY = "testKey";
    private static final String TEST_ID = "testId";
    private static final String TEST_TYPE = "testType";
    private static final Map<String, String> TEST_ATTRIBUTES = ImmutableMap.of("testKey", "testValue");

    @Mock
    private ProductCandidate productCandidate;

    private Validator validator = new Validator();

    @Test
    void givenCandidateWithAllAttributesShouldReturnValid() {
        when(productCandidate.getKey()).thenReturn(TEST_KEY);
        when(productCandidate.getIdentifier()).thenReturn(TEST_ID);
        when(productCandidate.getType()).thenReturn(TEST_TYPE);
        when(productCandidate.getSimpleAttributes()).thenReturn(TEST_ATTRIBUTES);

        Validation<Error, Product> result = validator.validate(productCandidate);
        assertTrue(result.isValid());

        Product product = result.get();
        assertAll(
                () -> assertEquals(TEST_KEY, product.getKey()),
                () -> assertEquals(TEST_ID, product.getIdentifier()),
                () -> assertEquals(TEST_TYPE, product.getType()),
                () -> assertEquals(TEST_ATTRIBUTES, product.getSimpleAttributes())
        );
    }

    @Test
    void givenCandidateWithAllMandatoryAttributesShouldReturnValid() {
        when(productCandidate.getKey()).thenReturn(TEST_KEY);
        when(productCandidate.getIdentifier()).thenReturn(TEST_ID);
        when(productCandidate.getType()).thenReturn(TEST_TYPE);
        when(productCandidate.getSimpleAttributes()).thenReturn(Collections.emptyMap());

        Validation<Error, Product> result = validator.validate(productCandidate);
        assertTrue(result.isValid());

        Product product = result.get();
        assertAll(
                () -> assertEquals(TEST_KEY, product.getKey()),
                () -> assertEquals(TEST_ID, product.getIdentifier()),
                () -> assertEquals(TEST_TYPE, product.getType()),
                () -> assertEquals(Collections.emptyMap(), product.getSimpleAttributes())
        );
    }

    @Test
    void givenCandidateWithoutKeyShouldReturnInvalid() {
        when(productCandidate.getIdentifier()).thenReturn(TEST_ID);
        when(productCandidate.getType()).thenReturn(TEST_TYPE);
        when(productCandidate.getSimpleAttributes()).thenReturn(TEST_ATTRIBUTES);

        Validation<Error, Product> result = validator.validate(productCandidate);
        assertFalse(result.isValid());
    }

    @Test
    void givenCandidateWithoutIdShouldReturnInvalid() {
        when(productCandidate.getKey()).thenReturn(TEST_KEY);
        when(productCandidate.getType()).thenReturn(TEST_TYPE);
        when(productCandidate.getSimpleAttributes()).thenReturn(TEST_ATTRIBUTES);

        Validation<Error, Product> result = validator.validate(productCandidate);
        assertFalse(result.isValid());
    }

    @Test
    void givenCandidateWithoutTypeShouldReturnInvalid() {
        when(productCandidate.getKey()).thenReturn(TEST_KEY);
        when(productCandidate.getIdentifier()).thenReturn(TEST_ID);
        when(productCandidate.getSimpleAttributes()).thenReturn(TEST_ATTRIBUTES);

        Validation<Error, Product> result = validator.validate(productCandidate);
        assertFalse(result.isValid());
    }
}