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
 reader will be transformed output from reading on the given Reader.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.8
 * @see   ChainedCharTransformer
 * @see   TransformingWriter
 */

public class TransformingReader extends PipedReader {

    private static final Logger log = Logging.getLoggerInstance(TransformingReader.class);

    private Reader in;
    private Runnable link;


    public TransformingReader(Reader in, CharTransformer charTransformer)  {
        super();
        this.in = in;
        PipedWriter w = new PipedWriter();
        try {            
            connect(w);
            link = new ChainedCharTransformer.TransformerLink(charTransformer, in, w, true);
            ChainedCharTransformer.executor.execute(link);          
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     * ALso closes the wrapped Reader.
     */   
    public void close() throws IOException {   
        super.close();
        synchronized(link) {
        }
        in.close();
    }
   
   

}

