package com.example.uploaderclient.api.boundary;

import java.util.Map;

/**
 * Object that contains information about upload progress.
 */
public interface UploadInfo {

    /**
     * String that informs about type of this {@code UploadInfo}.
     * @return this object's header, <b>never blank</b>
     */
    String header();

    /**
     * Key-value pairs containing detailed information about upload state.
     * @return this object's body, <b>never null</b>
     */
    Map<String, String> body();
}