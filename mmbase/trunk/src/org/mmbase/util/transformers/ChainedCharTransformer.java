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
            Reader r = null; 
            Writer w = endWriter;
            ListIterator i = charTransformers.listIterator(charTransformers.size());
            CharTransformer ct = null;
            while (i.hasPrevious()) {         
                ct = (CharTransformer) i.previous();
                if (i.hasPrevious()) { // needing a new Thread!
                    r = new PipedReader();
                    Thread thread =  new TransformerLink(ct, r, w);
                    w = new PipedWriter((PipedReader) r);
                    thread.start();
                } else {
                    r = startReader;
                }
            }
            // assert(r == startReader);
            if (ct != null) {
                ct.transform(startReader, w);

                w.flush();
                startReader.close();
                if (w instanceof PipedWriter) {
                    w.close();
                }
            }
        } catch (IOException e) {
            log.error(e.toString());
            log.debug(Logging.stackTrace(e));
        }
        return endWriter;        
    }

    public String toString() {
        return "CHAINED"  + charTransformers;
    }

    private class TransformerLink extends Thread {
        CharTransformer charTransformer;
        Writer     writer;
        Reader     reader;
        TransformerLink(CharTransformer ct, Reader r, Writer w) throws IOException {
            reader = r;
            writer = w;
            charTransformer = ct;
        }

        public void run() {
            try {
                charTransformer.transform(reader, writer);
                writer.flush();
                reader.close(); // Always a PipedReader
                if (writer instanceof PipedWriter) {
                    writer.close();
                }
            } catch (IOException io) {
            }
        }

    }

    public static void main(String[] args) {
        ChainedCharTransformer t = new ChainedCharTransformer();
        t.add(new SpaceReducer());
        t.add(new CopyCharTransformer());
        System.out.println("Starting transform");
        t.transform(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
        //StringWriter w = new StringWriter();
        //t.transform(new StringReader("hello      world"), w);
        //System.out.println(w.toString());
        System.out.println("Finished transform");
 
    }
    
}
