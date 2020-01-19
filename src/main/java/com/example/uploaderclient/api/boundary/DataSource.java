package com.example.uploaderclient.api.boundary;

import com.example.uploaderclient.parser.core.boundary.Parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple abstraction representing source of data consumed by the {@link Parser}.
 */
public interface DataSource {

    /**
     * Returns name of this {@code DataSource}, i.e. filename, URI.
     * @return name of this {@code DataSource}, <b>never blank</b>
     */
    String getName();

    /**
     * Opens an {@code InputStream} for a {@code Parser} to read from. Implementations are required
     * to provide new instance of the {@code InputStream} each time this method is invoked.
     * @return new, unread {@code InputStream}
     * @throws IOException in case any I/O problem occurs
     */
    InputStream openStream() throws IOException;
}