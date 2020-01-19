package com.example.uploaderclient.model.entity;

import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
public class Product {

    @NonNull
    String key;

    @NonNull
    String type;

    @NonNull
    String identifier;

    @NonNull
    Map<String, String> simpleAttributes;
}