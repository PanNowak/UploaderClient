package com.example.uploaderclient.parser.validation.control;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.core.entity.ProductCandidate;
import com.example.uploaderclient.parser.validation.entity.Error;

import io.vavr.control.Validation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Validator {

    private static final String BLANK_VALUE_ERROR_MESSAGE = "Parameter %s is mandatory and must not be blank!";

    public Validation<Error, Product> validate(ProductCandidate intermediateObject) {
        return validateNotBlank("key", intermediateObject.getKey())
                .combine(validateNotBlank("type", intermediateObject.getType()))
                .combine(validateNotBlank("identifier", intermediateObject.getIdentifier()))
                .combine(Validation.valid(intermediateObject.getSimpleAttributes()))
                .ap(Product::new)
                .mapError(errors -> new Error(intermediateObject, errors));
    }

    private Validation<String, String> validateNotBlank(String parameterName, String parameterValue) {
        if (StringUtils.isNotBlank(parameterValue)) {
            return Validation.valid(parameterValue);
        }

        String errorMessage = String.format(BLANK_VALUE_ERROR_MESSAGE, parameterName);
        return Validation.invalid(errorMessage);
    }
}