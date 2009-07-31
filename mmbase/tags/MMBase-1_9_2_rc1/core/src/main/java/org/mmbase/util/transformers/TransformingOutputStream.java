/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

import org.mmbase.util.logging.*;

/**
 * A Filtering OutputStream based on ByteTransformers.

<pre>

  ____  _________
 /    \/         \
 |this - PI --> O |
 | PO  |    T     |
 \____/ \________/


  PI: piped inputstream, this PO: this outputstream, T: transformer

  </pre>

  * @see TransformingWriter
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 */

public class TransformingOutputStream extends PipedOutputStream {

    private static final Logger log = Logging.getLoggerInstance(TransformingOutputStream.class);

    final private OutputStream out;
    final private ByteTransformerLink link;


    public TransformingOutputStream(OutputStream out, ByteTransformer transformer)  throws IOException {
        super();
        this.out = out;

        PipedInputStream r = new PipedInputStream();
        link = new ByteTransformerLink(transformer, r, out, false);
        connect(r);
        org.mmbase.util.ThreadPools.filterExecutor.execute(link);
    }

    protected void waitUntilReady() throws IOException {
        super.close(); // accept no more input
        try {
            while (! link.ready()) {
                synchronized(link) { // make sure we have the lock
                    link.wait();
                }
            }
        } catch (InterruptedException ie) {
            log.warn("" + ie);
        }
    }

    /**
     * {@inheritDoc}
     * Also closes the wrapped Writer.
     */
    public void close() throws IOException {
        waitUntilReady();
        out.close();

    }



}

