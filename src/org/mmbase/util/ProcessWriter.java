/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.io.*;
import org.mmbase.util.logging.*;

/**
 * A class to pipe data from one stream to the other as a thread.
 * Useful for building execution pipes.
 *
 * @deprecated-now 
 * 	External process streams are handled by the org.mmbase.util.externalprocess package
 * 	The StreamCopyThread does the same thing as this class
 *
 * @author Kees Jongenburger
 * @version $Id: ProcessWriter.java,v 1.7 2003-05-12 11:06:56 nico Exp $
 */
public class ProcessWriter implements Runnable{

    // logger
    private static Logger log = Logging.getLoggerInstance(ProcessWriter.class.getName());

    /**
     * The stream from which to pipe the data.
     */
    InputStream in ;
    /**
     * The stream to pipe the data to.
     */
    OutputStream out;
    /**
     * Size of the blocks in which data is piped
     */
    int blocksize=1024;

    /**
     * The background thread in which the piping process runs.
     */
    Thread runner=null;

    /**
     * Creates a writer to pipe data.
     * Uses a default blocksize of 1024.
     * @param in The stream from which to pipe the data.
     * @param out The stream to pipe the data to.
     */
    public ProcessWriter(InputStream in, OutputStream out) {
        this(in,out,1024);
    }

    /**
     * Creates a writer to pipe data.
     * @param in The stream from which to pipe the data.
     * @param out The stream to pipe the data to.
     * @param blocksize Size of the blocks in which data is piped
     */
    public ProcessWriter(InputStream in, OutputStream out,int blocksize) {
        this.in = in;
        this.out = out;
        this.blocksize = blocksize;
    }

    /**
     * Starts the piping process by creating the thread and running it.
     */
    public void start() {
        if (runner==null) {
            runner=new Thread(this);
            runner.start();
        }
    }

    /**
     * Performs the piping process.
     */
    public void run() {
        if (out != null && in != null) {
            try {
                PrintStream printStream = new PrintStream(out);
                byte[] data = new byte[blocksize];
                int size;
                int total = 0;
                while((size = in.read(data)) >0 ) {
                    total += size;
                    log.debug("wrote "+ size + " bytes (total:"+total+")");
                    printStream.write(data,0,size);
                    printStream.flush();
                }
                printStream.close();
            } catch (IOException e) {
                log.error("failed retieving information with reason: '" + e.getMessage() + "'");
                log.error(Logging.stackTrace(e));
            }
        } 
        else {
            if (in == null ) {
                log.warn("Inputstream is null");
            }
            if (out == null ) {
                log.warn("Outputstream is null");
            }
        }
    }
}
