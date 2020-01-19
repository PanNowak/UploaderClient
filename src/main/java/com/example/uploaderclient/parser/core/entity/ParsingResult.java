package com.example.uploaderclient.parser.core.entity;

import com.example.uploaderclient.model.entity.Product;
import com.example.uploaderclient.parser.validation.entity.Error;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.NoSuchElementException;

/**
 * Represents either a {@link Product} or an {@link Error} depending on
 * whether validation ended up with a success or with a failure.
 */
public interface ParsingResult {

    boolean isSuccessful();

    Product getProduct();

    Error getError();

    @ToString
    @EqualsAndHashCode
    final class Success implements ParsingResult {

        private final Product product;

        public Success(Product product) {
            this.product = product;
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public Product getProduct() {
            return product;
        }

        @Override
        public Error getError() {
            throw new NoSuchElementException("Invoked 'getError' on successful ParsingResult!");
        }
    }

    @ToString
    @EqualsAndHashCode
    final class Failure implements ParsingResult {

        private final Error error;

        public Failure(Error error) {
            this.error = error;
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public Product getProduct() {
            throw new NoSuchElementException("Invoked 'getProduct' on failed ParsingResult!");
        }

        @Override
        public Error getError() {
            return error;
        }
    }
}