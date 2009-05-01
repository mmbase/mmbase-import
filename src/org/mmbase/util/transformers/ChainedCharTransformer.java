/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.util.*;
import org.mmbase.util.ThreadPools;

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
 * @version $Id$
 */

public class ChainedCharTransformer extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(ChainedCharTransformer.class);
   
    private List<CharTransformer> charTransformers = new ArrayList<CharTransformer>();

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
     * @throws ClassCastException if collection does not contain only CharTransformers
     */
    public ChainedCharTransformer addAll(Collection<CharTransformer> col) {
        for (CharTransformer c : col) {
            add(c);
        }
        return this;
    }

    /**
     * @since MMBase-1.9
     */
    public ChainedCharTransformer add(CharTransformer... col) {
        for (CharTransformer c : col) {
            add(c);
        }
        return this;
    }


    /** 
     * Implementation without Threads. Not needed when transforming by String. 
     */
    public String transform(String string) {
        for (CharTransformer ct : charTransformers) {
            string = ct.transform(string);            
        }
        return string;
        
    }

    // javadoc inherited
    public Writer transform(Reader startReader, Writer endWriter) {
        try {
            PipedReader r = null; 
            Writer w = endWriter;  
            boolean closeWriterAfterUse = false; // This boolean indicates if 'w' must be flushed/closed after use.

            List<CharTransformerLink> links = new ArrayList<CharTransformerLink>();
            // keep track of the started threads, needing to wait for them later.

            // going to loop backward through the list of CharTransformers, and starting threads for
            // every transformation, besides the last one (which is the first in the chain). This
            // transformation is performed, and the then started other Threads catch the result.

            ListIterator<CharTransformer> i = charTransformers.listIterator(charTransformers.size());
            while (i.hasPrevious()) {         
                CharTransformer ct = i.previous();
                if (i.hasPrevious()) { // needing a new Thread!
                    r = new PipedReader();
                    CharTransformerLink link =  new CharTransformerLink(ct, r, w, closeWriterAfterUse);
                    links.add(link);
                    w = new PipedWriter(r);  
                    closeWriterAfterUse = true;
                    ThreadPools.filterExecutor.execute(link);
                } else {  // arrived at first in chain, start transforming
                    ct.transform(startReader, w);
                    if (closeWriterAfterUse) {
                        w.close();
                    }
                }
            }
            // wait until all threads are ready, because only then this transformation is actually
            // ready
            for (CharTransformerLink l : links) {
                try {
                    while (!l.ready()) {                            
                        synchronized(l) { // make sure we have the lock.
                            l.wait();
                        }
                    }
                } catch (InterruptedException ie) {
                    log.warn("" + ie);
                }
            }
        } catch (IOException e) {
            log.error(e.toString());
            log.info(Logging.stackTrace(e));
        }
        return endWriter;        
    }

    public String toString() {
        return "CHAINED"  + charTransformers;
    }



    // main for testing purposes
    public static void main(String[] args) throws IOException {
        ChainedCharTransformer t = new ChainedCharTransformer().add(new UnicodeEscaper()).add(new SpaceReducer()).add(new UpperCaser()).add(new Trimmer());
        System.out.println("Starting transform");
        
        t.transform(new InputStreamReader(System.in), new OutputStreamWriter(System.out)).flush();
        //System.out.println(t.transform(new StringReader("hello      world")));

        System.out.println(t.transform("test test   test test "));

        System.out.println("Finished transform");
 
    }
    
}
