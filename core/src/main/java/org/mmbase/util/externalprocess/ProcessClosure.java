/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.InputStream;
import java.io.OutputStream;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ProcessClosure handles the reading of the stdout and stderr of a external
 * process. A reader can block the calling thread until it is finished reading
 * (readBlocking). Or the reader can immidiately return when reading of the
 * stdout and stderr begins (readNonBlocking).
 *
 * ProcessClosure handles the writing of the stdin of a external process. A
 * writer can block the calling thread until it is finished writing to the stdin
 * and reading the stdout and stderr (writeBlocking). Or the writer can
 * imidiately return when writing to stdin and reading of the stdout and sterr
 * begins (writeNonBlocking)
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id$
 * @since MMBase-1.6
 */
public class ProcessClosure {

    private static final Logger log = Logging.getLoggerInstance(ProcessClosure.class);

    /**
     * the name of the process closure
     */
    protected String name;

    /**
     * the process object representing the external process
     */
    protected Process process;

    /**
     * The stream where data is read from to pipe it to stdin
     */
    protected InputStream input;
    /**
     * The stream where data is written to when piped from stdout
     */
    protected OutputStream output;
    /**
     * The stream where data is written to when piped from stderr
     */
    protected OutputStream error;

    /**
     * Thread for copying bytes from input to stdin
     */
    protected StreamCopyThread inputWriter;
    /**
     * Thread for copying bytes from stdout to output
     */
    protected StreamCopyThread outputReader;
    /**
     * Thread for copying bytes from stderr to error
     */
    protected StreamCopyThread errorReader;

    /**
     * Creates a process reader
    * .
    * @param name the name of the reader
    * @param inputStream process stdin is read from this stream. Can be
    * <code>null</code>, if not interested in writing the input
     * @param outputStream process stdout is written to this stream. Can be
     * <code>null</code>, if not interested in reading the output
     * @param errorStream porcess stderr is written to this stream. Can be
     * <code>null</code>, if not interested in reading the output
     */
    public ProcessClosure(
        String name,
        Process process,
        InputStream inputStream,
        OutputStream outputStream,
        OutputStream errorStream) {
        this.name = name;
        this.process = process;
        this.input = inputStream;
        this.output = outputStream;
        this.error = errorStream;
    }

    /**
    * read data from the external process without blocking the calling thread
    */
    public void readNonBlocking() {
        log.debug(name + " read Non Blocking");

        ThreadGroup group = new ThreadGroup(name + " ThreadGroup");

        //Reading both stdout and stderr is required to prevent deadlocks from
        //the external process.

        //External process Streams are seen from the point of view of the java process
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        outputReader = new StreamCopyThread(group, name + "OutputReader", stdout, output, false);
        errorReader = new StreamCopyThread(group, name + "ErrorReader", stderr, error, false);

        outputReader.start();
        errorReader.start();
    }

    /**
     * write data to the external process without blocking the calling thread
     */
    public void writeNonBlocking() {
        log.debug(name + " write Non Blocking");
        //External process Streams are seen from the point of view of the java process
        if (input != null) {
            OutputStream stdin = process.getOutputStream();

            inputWriter = new StreamCopyThread(name + "InputWriter", input, stdin, true);

            inputWriter.start();
        }
    }

    /**
     * read data from the external process and block the calling thread until
     * reading is finished
     */
    public void readBlocking() {
        log.debug(name + " read Blocking");
        readNonBlocking();

        log.debug(name + " wait for process");
        waitForProcess();
        waitForReaders();

        // it seems that thread termination and stream closing is working without
        // any help
        process = null;
        outputReader = null;
        errorReader = null;
        log.debug(name + " read done");
    }

    /**
     * write data to the external process and block the calling thread until
     * writing and reading is finished
     */
    public void writeBlocking() {
        log.debug(name + " write Blocking");

        //Reading both stdout and stderr is required to prevent deadlocks from
        //the external process.
        readNonBlocking();
        writeNonBlocking();

        log.debug(name + " wait for process");
        waitForWriter();
        waitForProcess();
        waitForReaders();

        // it seems that thread termination and stream closing is working without
        // any help
        process = null;
        outputReader = null;
        errorReader = null;
        inputWriter = null;
        log.debug(name + " write done");
    }

    /**
     * wait for the external process.to end
     */
    protected void waitForProcess() {
        boolean finished = false;
        while (!finished) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.service("Interrupted");
                return;
            }
            try {
                process.exitValue();
                finished = true;
            } catch (IllegalThreadStateException e) {
                //System.err.println("exception " +e);
            }
        }
    }

    /**
     * wait for the reading threads to finish copying
     */
    protected void waitForReaders() {
        // double-check using output threads
        if (!outputReader.finished()) {
            outputReader.waitFor();
        }

        if (!errorReader.finished()) {
            errorReader.waitFor();
        }
    }

    /**
     * wait for the writing thread to finish copying
     */
    protected void waitForWriter() {
        // double-check using input threads
        if (!inputWriter.finished()) {
            inputWriter.waitFor();
        }
    }

    /**
    * Process closure is alive when the external process and writer/reader
    * threads are still busy
    * @return <code>true</code> if is alive and <code>false</code> otherwise
    */
    public boolean isAlive() {
        if (process != null) {
            if (outputReader.isAlive() || errorReader.isAlive()) {
                return true;
            } else {
                process = null;
                outputReader = null;
                errorReader = null;
            }
        }
        return false;
    }

    /**
     * Forces the termination of the launched process
     */
    public void terminate() {
        if (process != null) {
            process.destroy();
            process = null;
        }
    }

}
