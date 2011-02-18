/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import java.io.*;
import java.nio.channels.*;

import org.mmbase.util.logging.*;

/**
 * Various utils to concisely and efficiently deal with streams
 * @since MMBase-1.9.1
 * @version $Id$
 */

public final class IOUtil {

    private static final Logger log = Logging.getLoggerInstance(IOUtil.class);

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOUtil() {
        // nothing, only static methods.
    }

    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     * @param input
     * @param output
     * @return
     * @throws java.io.IOException
     */
    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        if (input instanceof FileInputStream && output instanceof FileOutputStream) {
            log.debug("Streams are for files, using nio.");
            return copy((FileInputStream) input, (FileOutputStream) output);
        } else {
            return copy(input, output, DEFAULT_BUFFER_SIZE);
        }
    }

    public static long copy(final FileInputStream input, final FileOutputStream output) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = input.getChannel();
            destination = output.getChannel();
            log.debug("Copying " + source.size() + " bytes");
            destination.transferFrom(source, 0, source.size());
            return source.size();
        } finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
    /**
     * @param from
     * @param to
     * @since MMBase-2.0
     * @throws java.io.IOException
     * @return
     */
    public static long copy(final File from, final File to) throws IOException {
        FileInputStream in = new FileInputStream(from);
        FileOutputStream out = new FileOutputStream(to);
        try {
            long result = copy(in, out);
            return result;
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }

    }


    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     *
     * @param input
     * @param output
     * @param bufferSize
     *           Size of internal buffer to use.
     * @throws java.io.IOException
     * @return
     */
    public static long copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        long size = 0;
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer, 0, bufferSize))) {
            output.write(buffer, 0, n);
            size += n;
        }

        return size;
    }

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     * @param input
     * @param output
     * @return
     * @throws java.io.IOException
     */
    public static long copy(final Reader input, final Writer output) throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     *
     * @param input
     * @param output
     * @param bufferSize
     *           Size of internal buffer to use.
     * @return
     * @throws java.io.IOException
     */
    public static long copy(final Reader input, final Writer output, final int bufferSize) throws IOException {
        long size = 0;
        final char[] buffer = new char[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            size += n;
        }
        output.flush();

        return size;
    }

}
