package com.example.uploaderclient.parser.core.entity;

import com.example.uploaderclient.model.entity.Product;

import java.util.Map;

/**
 * Represents {@link Product} whose content is yet to be validated.
 */
public interface ProductCandidate {

    String getIdentifier();

    String getKey();

    String getType();

    Map<String, String> getSimpleAttributes();
}