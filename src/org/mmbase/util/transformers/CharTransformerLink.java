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
 * A Runnable implementation to perform a CharTransform between a Reader and a Writer.  This is used
 * in {@link ChainedCharTransformer}, {@link TransformingWriter} and {@link TransformingReader},
 * because those need a thread for each step of the transformation.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id$
 */

public class CharTransformerLink implements Runnable {

    private static Logger log = Logging.getLoggerInstance(CharTransformerLink.class);

    private CharTransformer charTransformer;
    private Writer     writer;
    private Reader     reader;
    private boolean    closeWriter;
    private boolean    ready = false;
    
    public CharTransformerLink(CharTransformer ct, Reader r, Writer w, boolean cw) {
        reader = r;
        writer = w;
        charTransformer = ct;
        closeWriter = cw;
    }
    
    synchronized public  void run() {            
        try {
            charTransformer.transform(reader, writer);       
            if (closeWriter) {
                writer.close();
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
