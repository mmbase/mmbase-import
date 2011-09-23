/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The command launcher provides a way to comunicate with a external process
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id:
 * @since MMBase-1.6
 */
public class CommandLauncher {


    private static final Logger log = Logging.getLoggerInstance(CommandLauncher.class);

    /**
     * Default buffer size.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * The number of milliseconds to pause between polling.
     */
    protected static final long DELAY = 50L;

    /**
     * Counts how many comands are launched
     * Also used for () identification
     */
    private static int counter = 0;

    /**
     * The process object representing the external process
     */
    protected Process process;

    /**
     * Command and arguments
     */
    protected String[] commandArgs;

    /**
     * The internal name of the external process
     */
    protected String name = "";

    /**
     * System line separator
     */
    private String lineSeparator;

    /**
     * Creates a new launcher
     * Fills in stderr and stdout output to the given streams.
     * Streams can be set to <code>null</code>, if output not required
     *
     * @param name internal name of the external process
     */
    public CommandLauncher(String name) {
        process = null;
        this.name = counter++ +" " + name;
        lineSeparator = System.getProperty("line.separator", "\n");
    }

    /**
     * get CommandArgs.
     * @return String[]
     */
    public String[] getCommandArgs() {
        return commandArgs;
    }

    /**
     * Constructs a command array that will be passed to the process
     *
     * @param command path of comand
     * @param commandArgs arguments after the command
     */
    protected String[] constructCommandArray(String command, String[] commandArgs) {

        String[] args = new String[1 + commandArgs.length];
        args[0] = command;
        System.arraycopy(commandArgs, 0, args, 1, commandArgs.length);
        return args;
    }

    /**
     * Execute a command
     *
     * @param command command
     * @throws IOException if an I/O error occurs
     */
    public void execute(String command) throws ProcessException {

        if (log.isDebugEnabled()) {
            printCommandLine(new String[] { command });
        }
        try {
            process = ProcessFactory.getFactory().exec(command);
        } catch (IOException e) {
            throw new ProcessException("An I/O error occured: " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new ProcessException("A security manager exists and its checkExec method doesn't allow creation of a subprocess.", e);
        } catch (NullPointerException e) {
            throw new ProcessException("Command is null.", e);
        } catch (IllegalArgumentException e) {
            throw new ProcessException("Command is empty.", e);
        }
    }

    /**
     * Execute a command
     *
     * @param commandArgs command and arguments
    * @throws IOException if an I/O error occurs
     */
    public void execute(String[] commandArgs) throws ProcessException {
        if (log.isDebugEnabled()) {
            printCommandLine(commandArgs);
        }
        try {
            process = ProcessFactory.getFactory().exec(commandArgs);
        } catch (IOException e) {
            throw new ProcessException("An I/O error occured: " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new ProcessException("A security manager exists and its checkExec method doesn't allow creation of a subprocess.", e);
        } catch (NullPointerException e) {
            throw new ProcessException("Command is null.", e);
        } catch (IllegalArgumentException e) {
            throw new ProcessException("Command is empty.", e);
        }
    }

    /**
     * Execute a command
     *
     * @param commandPath path of comand
     * @param args arguments after the command
     * @throws IOException if an I/O error occurs
     */
    public void execute(String commandPath, String[] args) throws ProcessException {
        commandArgs = constructCommandArray(commandPath, args);
		execute(commandArgs);
    }

    /**
     * Execute a command
     *
     * @param commandArgs command and arguments
     * @param env environment name value pairs
     * @throws IOException if an I/O error occurs
     */
    public void execute(String[] commandArgs, String[] env) throws ProcessException {

        if (log.isDebugEnabled()) {
            printCommandLine(commandArgs);
        }
        try {
            process = ProcessFactory.getFactory().exec(commandArgs, env);
        } catch (IOException e) {
            throw new ProcessException("An I/O error occured: " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new ProcessException("A security manager exists and its checkExec method " +
                                       "doesn't allow creation of a subprocess.", e);
        } catch (NullPointerException e) {
            throw new ProcessException("Command is null.", e);
        } catch (IllegalArgumentException e) {
            throw new ProcessException("Command is empty.", e);
        }
    }

    /**
     * Execute a command
     *
     * @param commandPath path of comand
     * @param args arguments after the comand
     * @param env environment name value pairs
     * @throws IOException if an I/O error occurs
     */
    public void execute(String commandPath, String[] args, String[] env) throws ProcessException {

        commandArgs = constructCommandArray(commandPath, args);
        execute(commandArgs, env);
    }

    /**
     * Execute a command
     *
     * @param commandArgs command and arguments
     * @param env environment name value pairs
     * @param changeToDirectory working directory
     * @throws IOException if an I/O error occurs
     */
    public void execute(String[] commandArgs, String[] env, String changeToDirectory) throws ProcessException {

        if (log.isDebugEnabled()) {
            printCommandLine(commandArgs);
        }
        try {
            process = ProcessFactory.getFactory().exec(commandArgs, env, changeToDirectory);
        } catch (IOException e) {
            throw new ProcessException("An I/O error occured: " + e.getMessage());
        } catch (SecurityException e) {
            throw new ProcessException(
                "A security manager exists and its checkExec method " + "doesn't allow creation of a subprocess.");
        } catch (NullPointerException e) {
            throw new ProcessException("Command is null.");
        } catch (IllegalArgumentException e) {
            throw new ProcessException("Command is empty.");
        }
    }

    /**
     * Execute a command
     *
     * @param commandPath path of comand
     * @param args arguments after the comand
     * @param env environment name value pairs
     * @param changeToDirectory working directory
     * @throws IOException if an I/O error occurs
     */
    public void execute(String commandPath, String[] args, String[] env, String changeToDirectory)
        throws ProcessException {

        commandArgs = constructCommandArray(commandPath, args);
        execute(commandArgs, env, changeToDirectory);
    }

    /**
     * Reads output from the external process to the streams.
     *
     * @param out process stdout is written to this stream
     * @param err process stderr is written to this stream
     * @throws ProcessException if process not yet executed
     */
    public void waitAndRead(OutputStream out, OutputStream err) throws ProcessException {
        if (process == null) {
            throw new ProcessException("Process not yet executed");
        }

        ProcessClosure reader = new ProcessClosure(name, process, null, out, err);
        reader.readBlocking(); // a blocking call
    }

    /**
     * Reads output from the external process to the streams. A progress monitor
     * is polled to test for cancellation. Destroys the process if the monitor
     * becomes cancelled
     *
     * @param output process stdout is written to this stream
     * @param err process stderr is written to this stream
     * @param monitor monitor monitor to receive progress info and to cancel
     *    the  external process
     * @throws ProcessException if process not yet executed or if process
     * cancelled
     */
    public void waitAndRead(OutputStream output, OutputStream err, IProgressMonitor monitor) throws ProcessException {
        if (process == null) {
            throw new ProcessException("Process not yet executed");
        }

        PipedOutputStream errOutPipe = new PipedOutputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();
        PipedInputStream errInPipe, inputPipe;
        try {
            errInPipe = new PipedInputStream(errOutPipe);
            inputPipe = new PipedInputStream(outputPipe);
        } catch (IOException e) {
            throw new ProcessException("Command canceled");
        }

        ProcessClosure closure = new ProcessClosure(name, process, null, outputPipe, errOutPipe);
        closure.readNonBlocking();

        processStreams(closure, output, inputPipe, err, errInPipe, monitor);
    }

    /**
     * Writes input to and reads output from the external process to the streams.
     *
     * @param in process stdin is read from this stream
     * @param out process stdout is written to this stream
     * @param err process stderr is written to this stream
     * @throws ProcessException if process not yet executed
     */
    public ProcessClosure waitAndWrite(InputStream in, OutputStream out, OutputStream err) throws ProcessException {
        if (process == null) {
            throw new ProcessException("Process not yet executed");
        }

        ProcessClosure reader = new ProcessClosure(name, process, in, out, err);
        reader.writeBlocking(); // a blocking call
        return reader;
    }

    /**
     * Writes input to and reads output from the external process to the streams.
     * A progress monitor is polled to test for cancellation. Destroys the
     * process if the monitor becomes cancelled
     *
     * @param in process stdin is read from this stream
     * @param output process stdout is written to this stream
     * @param err process stderr is written to this stream
     * @param monitor monitor monitor to receive progress info and to cancel
     *    the  external process
     * @throws ProcessException if process not yet executed or if process
     * cancelled
     */
    public void waitAndWrite(InputStream in, OutputStream output, OutputStream err, IProgressMonitor monitor)
        throws ProcessException {
        if (process == null) {
            throw new ProcessException("Process not yet executed");
        }

        PipedOutputStream errOutPipe = new PipedOutputStream();
        PipedOutputStream outputPipe = new PipedOutputStream();
        PipedInputStream errInPipe, inputPipe;
        try {
            errInPipe = new PipedInputStream(errOutPipe);
            inputPipe = new PipedInputStream(outputPipe);
        } catch (IOException e) {
            throw new ProcessException("Command canceled");
        }

        ProcessClosure closure = new ProcessClosure(name, process, in, outputPipe, errOutPipe);
        closure.readNonBlocking();
        closure.writeNonBlocking();

        processStreams(closure, output, inputPipe, err, errInPipe, monitor);
    }

    /**
     * process the Streams.while the external process returns bytes. Cancellation
     * is possible by the ProgressMonitor
     *
     * @param closure process closure object which handles the interaction with
     *    the  external process
     * @param output process stdout is written to this stream
     * @param inputPipe piped stream to other thread for the stdout
     * @param err process stderr is written to this stream
     * @param errInPipe piped stream to other thread for the stderr
     * @param monitor monitor to receive progress info and to cancel
     *    the   external process
     * @throws ProcessException if process cancelled
     */
    protected void processStreams(
        ProcessClosure closure,
        OutputStream output,
        PipedInputStream inputPipe,
        OutputStream err,
        PipedInputStream errInPipe,
        IProgressMonitor monitor)
        throws ProcessException {

        monitor.begin();

        byte buffer[] = new byte[BUFFER_SIZE];
        int nbytes;
        while (!monitor.isCanceled() && closure.isAlive()) {
            nbytes = 0;
            try {
                if (errInPipe.available() > 0) {
                    nbytes = errInPipe.read(buffer);
                    err.write(buffer, 0, nbytes);
                    err.flush();
                }
                if (inputPipe.available() > 0) {
                    nbytes = inputPipe.read(buffer);
                    output.write(buffer, 0, nbytes);
                    output.flush();
                }
            } catch (IOException e) {
            }
            if (nbytes == 0) {
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException ie) {
                    break;
                }
            } else {
                monitor.worked();
            }
        }

        // Operation canceled by the user, terminate abnormally.
        if (monitor.isCanceled()) {
            closure.terminate();
            throw new ProcessException("Command canceled");
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            monitor.setCanceled(true);
            log.service("Interrupted");
        }

        // Drain the pipes.
        try {
            while (errInPipe.available() > 0 || inputPipe.available() > 0) {
                // Operation canceled by the user, terminate abnormally.
                if (monitor.isCanceled()) {
                    closure.terminate();
                    throw new ProcessException("Command canceled");
                }
                nbytes = 0;
                if (errInPipe.available() > 0) {
                    nbytes = errInPipe.read(buffer);
                    err.write(buffer, 0, nbytes);
                    err.flush();
                }
                if (inputPipe.available() > 0) {
                    nbytes = inputPipe.read(buffer);
                    output.write(buffer, 0, nbytes);
                    output.flush();
                }
                if (nbytes != 0) {
                    monitor.worked();
                }
            }
        } catch (IOException e) {} finally {
            try {
                errInPipe.close();
            } catch (IOException e) {}
            try {
                inputPipe.close();
            } catch (IOException e) {}
        }

        monitor.done();
    }

    /**
     * print Command Line.
     *
     * @param commandArgs array of comand and args
     */
    public void printCommandLine(String[] commandArgs) {
        StringBuilder buf = new StringBuilder();
        for (String element : commandArgs) {
            buf.append(element);
            buf.append(' ');
        }
        buf.append(lineSeparator);
        log.debug(buf.toString());
    }

    /**
     * @since MMBase-1.9.1
     */
    public Process getProcess() {
        return process;
    }
}
