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
 * A CharTransformer which wraps N other CharTransformers, and links them with N - 1 new Threads,
 * effectively working as a 'chained' transformer.
 * 
 * The first transformation is done by the ChainedCharTransformer instance itself, after starting
 * the N - 1 Threads for the other N - 1 transformations.
 *
 * If no CharTransformers are added, and 'transform' is called, logically, nothing will happen. Add
 * the CopyCharTransformer if necessary.
 *
 * Schematicly:
 * 
 <pre>

  new ChainedCharTransformer().add(T1).add(T2)....add(TN).transform(R, W);

  ___________  __________       _________
 /           \/          \     /         \
 |  R  --> PW - PR --> PW -...- PR --> W  |
 |     T1     |    T2     |    |   TN     |
 \___________/ \_________/     \_________/
  

 R: reader, PR: piped reader, W: writer, PW, piped writer, T1 - TN: transformers

  </pre>
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ChainedCharTransformer.java,v 1.15 2004-01-05 17:39:07 michiel Exp $
 */

public class ChainedCharTransformer extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(ChainedCharTransformer.class);

    private List charTransformers = new ArrayList();

    public ChainedCharTransformer() {
        super();
    }

    /**
     * Adds a CharTranformer to the chain of CharTransformers. If the
     * CharTransformer is a ChainedCharTransformer, then it will not
     * be added itself, but its elements will be added.
     */
    public ChainedCharTransformer add(CharTransformer ct) {
        if (ct instanceof ChainedCharTransformer) {
            addAll(((ChainedCharTransformer)ct).charTransformers);
        } else {
            charTransformers.add(ct);
        }
        return this;
    }

    /**
     * Adds a Collection of CharTranformers to the chain of CharTransformers.
     *
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

    // javadoc inherited
    public Writer transform(Reader startReader, Writer endWriter) {
        try {
            Reader r = null; 
            Writer w = endWriter;  
            boolean closeWriterAfterUse = false; // This boolean indicates if 'w' must be flushed/closed
                                                 // after use.

            List threads = new ArrayList(); // keep track of the started threads, needing to wait
                                            // for them later.

            // going to loop backward through the list of CharTransformers, and starting threads for
            // every transformation, besides the last one (which is the first in the chain). This
            // transformation is performed, and the then started other Threads catch the result.

            ListIterator i = charTransformers.listIterator(charTransformers.size());
            while (i.hasPrevious()) {         
                CharTransformer ct = (CharTransformer) i.previous();
                if (i.hasPrevious()) { // needing a new Thread!
                    r = new PipedReader();

                    Thread thread =  new TransformerLink(ct, r, w, closeWriterAfterUse);
                    thread.setDaemon(true);
                    if (log.isDebugEnabled()) log.debug("instantiated new tread " + thread);
                    threads.add(thread);

                    w = new PipedWriter((PipedReader) r);  
                   closeWriterAfterUse = true;

                    thread.start();
                } else {  // arrived at first in chain, start transforming
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

    /**
     * This Thread performs the transformations which are not the first. The Thread performing the
     * second transformation listens on the PipedReader which is connected to the PipedWriter to
     * which the first transformation is writing. If this transformation is the last, then it is
     * writing to the 'final' writer, otherwise it is writing to another PipedWriter, connecting it
     * to the next transformer in the chain.
     */
    static class TransformerLink extends Thread {
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
    

    // main for testing purposes
    public static void main(String[] args) throws IOException {
        ChainedCharTransformer t = new ChainedCharTransformer().add(new SpaceReducer()).add(new UpperCaser());
        System.out.println("Starting transform");
        
        t.transform(new InputStreamReader(System.in), new OutputStreamWriter(System.out)).flush();
        //System.out.println(t.transform(new StringReader("hello      world")));

        System.out.println("Finished transform");
 
    }
    
}
