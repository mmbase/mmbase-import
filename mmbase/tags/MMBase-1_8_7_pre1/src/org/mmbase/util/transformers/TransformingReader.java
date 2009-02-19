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
 * A Filtering Reader based on CharTransformers.
<pre>

  _________   ____  
 /          \/    \  
 | R --> PW - this |
 |    T     |  PR  |
 \_________/ \____/
  

  PW: piped writer, this PR: this reader, T: transformer

  </pre>
 * This reader can be instantiated with another Reader and a CharTransformer. All reading from this
 * reader will be transformed output from reading on the given Reader.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.8
 * @see   ChainedCharTransformer
 * @see   TransformingWriter
 */

public class TransformingReader extends PipedReader {

    private static final Logger log = Logging.getLoggerInstance(TransformingReader.class);

    private Reader in;
    private CharTransformerLink link;


    public TransformingReader(Reader in, CharTransformer charTransformer)  {
        super();
        this.in = in;
        PipedWriter w = new PipedWriter();
        try {            
            connect(w);
            link = new CharTransformerLink(charTransformer, in, w, true);
            org.mmbase.util.ThreadPools.filterExecutor.execute(link);          
        } catch (IOException ioe) {
            log.error(ioe.getMessage() + Logging.stackTrace(ioe));
        }
    }
    
    public synchronized int read() throws IOException {
        int result =  super.read();
        if (result == -1) { // nothing to read any more, wait until transformation is ready.
            waitReady();
        }
        return result;
    }

    public synchronized int read(char cbuf[], int off, int len)  throws IOException {
        int result = super.read(cbuf, off, len);
        if (result == -1) {
            waitReady();
        }
        return result;
    }

    /**
     * Wait until the transformation is ready
     */
    protected void waitReady() {
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
     * ALso closes the wrapped Reader.
     */   
    public void close() throws IOException {   
        log.debug("closing");
        super.close();
        in.close();
    }
   
   
    // main for testing purposes
    public static void main(String[] args) {

        String testString = "use argument to change this string";
        if (args.length > 0) {
            testString = args[0];
        }
 

        BufferedReader reader = new BufferedReader(new TransformingReader(new StringReader(testString), new UnicodeEscaper()));

        try {
            while(true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println(line);
            }
        } catch (Exception e) {
            log.error(e + Logging.stackTrace(e));
        }

        
        ChainedCharTransformer t = new ChainedCharTransformer();
        t.add(new UnicodeEscaper());
        t.add(new UpperCaser());
        t.add(new SpaceReducer());
        t.add(new Trimmer());
        
        reader = new BufferedReader(new TransformingReader(new StringReader(testString), t));
        
        try {
            while(true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println(line);
            }
        } catch (Exception e) {
            log.error(e + Logging.stackTrace(e));
        }
    }



}

