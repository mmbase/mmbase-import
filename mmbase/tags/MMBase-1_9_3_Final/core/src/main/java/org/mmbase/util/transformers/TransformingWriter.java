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
 * A Filtering Writer based on CharTransformers.

<pre>

  ____  _________
 /    \/         \
 |this - PR --> W |
 | PW  |    T     |
 \____/ \________/


  PR: piped reader, this PW: this writer, T: transformer

  </pre>
 * This writer can be instantiated with another Writer and a CharTransformer. All writing will be transformed by the given
 * CharTransformer before arriving at the given Writer.
 *
 * When ready, this TransformingWriter should be 'closed'. A coding example can be found in this classe's main method.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @see   ChainedCharTransformer
 * @see   TransformingReader
 */

public class TransformingWriter extends PipedWriter {

    private static final Logger log = Logging.getLoggerInstance(TransformingWriter.class);

    private Writer out;
    private CharTransformerLink link;


    public TransformingWriter(Writer out, CharTransformer charTransformer)  {
        super();
        this.out = out;

        PipedReader r = new PipedReader();
        try {
            connect(r);
            link = new CharTransformerLink(charTransformer, r, out, false);
            org.mmbase.util.ThreadPools.filterExecutor.execute(link);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
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


    // main for testing purposes
    public static void main(String[] args) {
        Writer out = new OutputStreamWriter(System.out);
        ChainedCharTransformer t = new ChainedCharTransformer();
        t.add(new UpperCaser());
        t.add(new SpaceReducer());
        t.add(new Trimmer());
        TransformingWriter writer = new TransformingWriter(out, t);
        String testString = "use argument to change this string";
        if (args.length > 0) {
            testString = args[0];
        }
        try {
            writer.write(testString);
            writer.close();

        } catch(Exception e) {
            log.error("" + e + Logging.stackTrace(e));
        }

        org.mmbase.util.ThreadPools.filterExecutor.shutdown();

    }




}

