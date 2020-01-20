package com.example.uploaderclient.api.boundary;

import java.io.IOException;
import java.io.InputStream;

import com.example.uploaderclient.parser.core.boundary.Parser;

/**
 * Simple abstraction representing source of data consumed by the {@link Parser}.
 */
public interface DataSource {

    /**
     * Returns full name of this {@code DataSource}, e.g. path to the file, URI.
     * @return full name of this {@code DataSource}, <b>never blank</b>
     */
    String getName();

    /**
     * Returns short representation of this {@code DataSource} name, e.g. filename
     * @return short name of this {@code DataSource}, <b>never blank</b>
     */
    String getShortName();

    /**
     * Opens an {@code InputStream} for a {@code Parser} to read from. Implementations are required
     * to provide new instance of the {@code InputStream} each time this method is invoked.
     * @return new, unread {@code InputStream}
     * @throws IOException in case any I/O problem occurs
     */
    InputStream openStream() throws IOException;
}