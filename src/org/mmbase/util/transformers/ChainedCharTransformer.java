/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * A CharTransformer which wraps N other CharTransformers, and links
 * them with N - 1 new Threads, effectively working as a 'chained'
 * transformer.
 * 
 * The Nth transformation is done by the ChainedCharTransformer
 * instance itself, after starting the N - 1 Threads for the other N -
 * 1 transformations.
 *
 * If no CharTransformers are added, and 'transform' is called,
 * logicly, nothing will happen. Add the CopyCharTransformer if
 * necessary.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 */

public class ChainedCharTransformer extends AbstractCharTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(ChainedCharTransformer.class.getName());

    private List charTransformers = new ArrayList();

    public ChainedCharTransformer() {
        super();
    }

    public ChainedCharTransformer add(CharTransformer ct) {
        charTransformers.add(ct);
        return this;
    }

    /**
     * @throws ClassCastException if collecion does not contain only CharTransformers
     */
    public ChainedCharTransformer addAll(Collection col) {
        Iterator i = col.iterator();
        while (i.hasNext()) {
            CharTransformer c = (CharTransformer) i.next();
            add(c);
        }
        return this;
    }

    public Writer transform(Reader startReader, Writer endWriter) {
        try {
            Reader r = startReader;
            Writer w = null;
            Iterator i = charTransformers.iterator();
            CharTransformer ct = null;
            while (i.hasNext()) {         
                ct = (CharTransformer) i.next();
                if (i.hasNext()) {
                    w = new PipedWriter();
                    TransformerLink thread =  new TransformerLink(ct, (PipedWriter) w);
                    r = thread.getReader();
                    thread.start();
                } else {
                    w = endWriter;
                }
            }
            assert(w == endWriter);
            if (ct != null) {
                ct.transform(r, endWriter);            
            }
        } catch (IOException e) {
            log.error(e.toString());
            log.debug(Logging.stackTrace(e));
        }
        return endWriter;        
    }

    public String toString() {
        return "CHAINED "  + charTransformers;
    }

    private class TransformerLink extends Thread {
        CharTransformer charTransformer;
        PipedWriter     pw;
        PipedReader     pr;
        TransformerLink(CharTransformer ct, PipedWriter w) throws IOException {
            pw = w;
            pr = new PipedReader(pw);
            charTransformer = ct;
        }
        public Reader getReader() {
            return pr;
        }

        public void run() {
            charTransformer.transform(pr, pw);
        }

    }

    public static void main(String[] args) {
        
    }
    
}
