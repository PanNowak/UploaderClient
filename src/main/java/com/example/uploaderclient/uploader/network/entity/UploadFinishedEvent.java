package com.example.uploaderclient.uploader.network.entity;

import com.example.uploaderclient.api.boundary.UploadInfo;
import com.google.common.collect.ImmutableMap;
import lombok.Value;

import java.util.Map;

@Value
public class UploadFinishedEvent implements UploadInfo {

    private final String statusCode;
    private final String statusText;

    @Override
    public String header() {
        return "Upload finished";
    }

    @Override
    public Map<String, String> body() {
        return ImmutableMap.of(
                "Status code", statusCode,
                "Status text", statusText);
    }
}