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
 * A Runnable implementation to perform a ByteTransformation between an InputStream and an OutputStream.  This is used
 * in {@link TransformingOutputStream}
 * because those need a thread for each step of the transformation.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9.2
 * @version $Id$
 */

public class ByteTransformerLink implements Runnable {

    private static Logger log = Logging.getLoggerInstance(ByteTransformerLink.class);

    private ByteTransformer transformer;
    private OutputStream     out;
    private InputStream     in;
    private boolean    closeOutputStream;
    private boolean    ready = false;

    public ByteTransformerLink(ByteTransformer t, InputStream i, OutputStream o, boolean co) {
        in = i;
        out = o;
        transformer = t;
        closeOutputStream = co;
    }

    synchronized public  void run() {
        try {
            transformer.transform(in, out);
            if (closeOutputStream) {
                out.close();
            }
        } catch (IOException io) {
            log.error(io.toString());
            log.error(io);
        }
        ready = true;
        notifyAll();
    }
    synchronized public boolean ready() {
        return ready;
    }
}
