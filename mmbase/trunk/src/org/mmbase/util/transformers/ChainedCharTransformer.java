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
            Writer  w = endWriter;  
            boolean closeWriterAfterUse = false;

            List threads = new ArrayList();          
            CharTransformer ct = null;

            ListIterator i = charTransformers.listIterator(charTransformers.size());
            while (i.hasPrevious()) {         
                ct = (CharTransformer) i.previous();
                if (i.hasPrevious()) { // needing a new Thread!
                    r = new PipedReader();

                    Thread thread =  new TransformerLink(ct, r, w, closeWriterAfterUse);
                    thread.setDaemon(true);
                    if (log.isDebugEnabled()) log.debug("instantiated new tread " + thread);
                    threads.add(thread);

                    w = new PipedWriter((PipedReader) r);  
                    closeWriterAfterUse = true;

                    thread.start();
                } else { 
                    ct.transform(startReader, w);
                    if (closeWriterAfterUse) {
                        w.flush();
                        w.close();
                    }
                }
            }
            // wait until all threads are ready, because only then this transformation is actually ready
            Iterator ti = threads.iterator();
            while (ti.hasNext()) {
                Thread t = (Thread) ti.next();
                t.join();
            }
        } catch (IOException e) {
            log.error(e.toString());
            log.debug(Logging.stackTrace(e));
        } catch (InterruptedException ie) {
            log.error(ie.toString());
            log.debug(Logging.stackTrace(ie));
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
        boolean    closeWriter;
        TransformerLink(CharTransformer ct, Reader r, Writer w, boolean cw) throws IOException {
            reader = r;
            writer = w;
            charTransformer = ct;
            closeWriter = cw;
        }

        public void run() {
            log.debug("starting thread");
            try {
                charTransformer.transform(reader, writer);
                if (closeWriter) {
                    writer.flush();
                    writer.close();
                }
                reader.close(); // Always a PipedReader
            } catch (IOException io) {
                log.error(io.toString());
            }
            log.debug("thread end");
        }

    }

    public static void main(String[] args) throws IOException {
        ChainedCharTransformer t = new ChainedCharTransformer().add(new SpaceReducer()).add(new UpperCaser());
        System.out.println("Starting transform");
        
        t.transform(new InputStreamReader(System.in), new OutputStreamWriter(System.out)).flush();
        //System.out.println(t.transform(new StringReader("hello      world"), new StringWriter()));

        System.out.println("Finished transform");
 
    }
    
}
