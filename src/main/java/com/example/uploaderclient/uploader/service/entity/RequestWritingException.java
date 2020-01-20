/* @(#) $$Id$$
 *
 * Copyright (c) 2000-2020 Comarch SA All Rights Reserved. Any usage,
 * duplication or redistribution of this software is allowed only according to
 * separate agreement prepared in written between Comarch and authorized party.
 */
package com.example.uploaderclient.uploader.service.entity;

/**
 * @author mnowak
 */
public class RequestWritingException extends /*IOException*/RuntimeException /*temporarily*/ {

    public RequestWritingException(String message, Throwable cause) {
        super(message, cause);
    }
}
