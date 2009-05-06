/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Thread which continuously reads from a input stream and pushes the read data
 * to an output stream which is immediately flushed afterwards.
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id$
 * @since MMBase-1.6
 */
class StreamCopyThread extends Thread {

    /** MMBase logging system */
    private static Logger log = Logging.getLoggerInstance(StreamCopyThread.class.getName());

    /**
     * The number of milliseconds to wait before writing
     */
    protected static final long WAIT_DELAY = 25;

    /**
     * Default buffer size.
     */
    public static final int BUFFER_SIZE = 1024;

    /**
     * The stream from which to pipe the data.
     */
    private InputStream inputStream;

    /**
     * The stream to pipe the data to.
     */
    private OutputStream outputStream;

    /**
     * This thread writes to the external process
     */
    private boolean processInput = false;

    /**
     * Stream copying finished
     */
    private boolean finished = false;

    /**
     * Create a thread to copy bytes fro one strea to the other
     * 
     * @param name the name of the new thread
     * @param in the stream from which to pipe the data
     * @param out the stream to pipe the data to
     * @param pInput This thread writes to the external process
     */
    public StreamCopyThread(String name, InputStream in, OutputStream out, boolean pInput) {
        this(null, name, in, out, pInput);
    }

    /**
     * Create a thread to copy bytes fro one stream to the other
     * 
     * @param group ThreadGroup where this thread belongs to
     * @param name the name of the new thread
     * @param in the stream from which to pipe the data
     * @param out the stream to pipe the data to
     * @param bSize the size of the buffer in which data is piped
     * @param pInput This thread writes to the external process
     */
    public StreamCopyThread(
        ThreadGroup group,
        String name,
        InputStream in,
        OutputStream out,
        boolean pInput) {
        super(group, name);
        processInput = pInput;
        outputStream = out;
        inputStream = in;
        setDaemon(true);
    }

    /**
    * @see java.lang.Runnable#run()
    */
    public void run() {
        BufferedInputStream reader = new BufferedInputStream(inputStream);
        BufferedOutputStream writer = null;
        if (outputStream != null) {
            writer = new BufferedOutputStream(outputStream);
        }
        try {
            /*
             * Without the sleep call, the subprocess doesn't get enough time
             * to get started before we try to read its output, so we got
             * nothing.  This is a kludge, but it achieves the desired effect.
             * Finding a more elegant solution is an exercise for the student,
             * as those horrible college math book authors loved to say.
             */
            try {
                Thread.sleep(WAIT_DELAY);
            } catch (InterruptedException e) {
                // we only wanted a delay
            }

            int size = 0;
            //this buffer has nothing to do with the OS buffer
            byte[] buffer = new byte[StreamCopyThread.BUFFER_SIZE];

            while ((size = reader.read(buffer)) != -1) {
                if (writer != null) {
                    writer.write(buffer, 0, size);
                    writer.flush();
                }
                //log.debug("StreamCopy " + this.getName() + " read " + size + " bytes from input and wrote to output." );

                //Maybe we should reset variables
                //size = 0;
                //buffer = new byte[BUFFER_SIZE];
            }
        } catch (IOException x) {
            // ignore
        } finally {
            /*
             This thread only closes the stream to the process.
             This way , the external process knows that we are finished writing.
             Closing the stdout and sterr is not critical, but it is still nice
             to close all the resources
             This thread is not responsible for closing the stream of the java process. 
            */
            if (processInput) {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    //ignore
                }
            } else {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    //ignore
                }
            }
            complete();
        }
    }

    /**
     * Returns whether this thread has finished copying bytes
     *
     * @return <code>true</code> if finished and <code>false</code> otherwise
     */
    public synchronized boolean finished() {
        return finished;
    }

    /**
    * By calling this method the calling thread will wait until this thread is
    * finished copying bytes
    */
    public synchronized void waitFor() {
        while (!finished) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }

    /**
    * This method is called when the copying of bytes is done and notifies a
    * waiting thread
    */
    protected synchronized void complete() {
        finished = true;
        notify();
        log.debug("StreamCopy " + this.getName() + " finished.");
    }
}