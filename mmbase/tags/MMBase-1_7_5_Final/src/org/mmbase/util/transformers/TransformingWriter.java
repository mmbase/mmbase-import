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
 * CharTransformer before ariving at the give Writer.
 *
 * When ready, this TransformingWriter should be 'closed'. A coding example can be found in this classe's main method.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @see   ChainedCharTransformer
 */

public class TransformingWriter extends PipedWriter {

    private static final Logger log = Logging.getLoggerInstance(TransformingWriter.class);


    private CharTransformer charTransformer;
    private Writer out;
    private Thread thread;


    public TransformingWriter(Writer out, CharTransformer ct)  {
        super();
        this.out = out;
        charTransformer = ct;
        PipedReader r = new PipedReader();
        try {            
            connect(r);
            thread =  new ChainedCharTransformer.TransformerLink(charTransformer, r, out, false);
            thread.setDaemon(true);
            if (log.isDebugEnabled()) log.debug("instantiated new tread " + thread);
            thread.start();
          
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }

   
    public void close() throws IOException {   
        try {
            super.close();
            thread.join();
        } catch (InterruptedException ie) {
            log.error(ie.getMessage());
        }
    }
   
  
    // main for testing purposes
    public static void main(String[] args) throws IOException {
        Writer end = new StringWriter();
        ChainedCharTransformer t = new ChainedCharTransformer();
        t.add(new UpperCaser());
        t.add(new SpaceReducer());
        TransformingWriter writer = new TransformingWriter(end, t);
        String testString = "use argument to change this string";
        if (args.length > 0) {
            testString = args[0];
        }
        try {
            System.out.println("Transforming '" + testString + "'");
            writer.write(testString);
            writer.close();
            System.out.println(end.toString());
        } catch(Exception e) {
            log.error("" + e + Logging.stackTrace(e));
        }
    }

    


}

