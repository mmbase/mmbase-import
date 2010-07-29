/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.File;
import java.io.IOException;

/**
 * Process Factory creates a external process.
 * 
 * The factory could be used to create a process in another way than java.lang.
 * Runtime.exec();
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id$
 * @since MMBase-1.6
 */
public class ProcessFactory {

    /**
     * instance of the Process Factory
     */
    static private ProcessFactory instance = new ProcessFactory();

    /**
     * Runtime of the current java process
     */
    private Runtime runtime;

    /**
    * get the Process Factory.instance
    * @return ProcessFactory
    */
    public static ProcessFactory getFactory() {
        return instance;
    }

    /**
     * Create an new process factory.
     */
    private ProcessFactory() {
        runtime = Runtime.getRuntime();
    }

    /**
     * Executes the specified command in a separate process.
     *
     * @param cmd the command to call
     * @return Process a Process object for managing the external process
     * @throws IOException if an I/O error occurs.
     */
    public Process exec(String cmd) throws IOException {
        return runtime.exec(cmd);
    }

    /**
     * Executes the specified command and arguments in a separate process.
     *
     * @param cmdarray array containing the command to call and its arguments
     * @return Process a Process object for managing the external process
     * @throws IOException if an I/O error occurs.
     */
    public Process exec(String[] cmdarray) throws IOException {
        return runtime.exec(cmdarray);
    }

    /**
     * Executes the specified command and arguments in a separate process with
     * the specified environment.
     *
     * @param cmdarray array containing the command to call and its arguments
     * @param envp array of strings, each element of which has environment
     *    variable  settings in format name=value.
     * @return Process a Process object for managing the external process
     * @throws IOException if an I/O error occurs.
     */
    public Process exec(String[] cmdarray, String[] envp) throws IOException {
        return runtime.exec(cmdarray, envp);
    }

    /**
     * Executes the specified command in a separate process with the specified
     * environment.
     *
     * @param cmd the command to call
     * @param envp array of strings, each element of which has environment
     *    variable  settings in format name=value.
     * @return Process a Process object for managing the external process
     * @throws IOException if an I/O error occurs.
     */
    public Process exec(String cmd, String[] envp) throws IOException {
        return runtime.exec(cmd, envp);
    }

    /**
     * Executes the specified command in a separate process with the specified
     * environment and working directory.
     *
     * @param cmd the command to call
     * @param envp array of strings, each element of which has environment
     *    variable  settings in format name=value.
     * @param dir the working directory of the subprocess
     * @return Process a Process object for managing the external process
     * @throws IOException if an I/O error occurs.
     */
    public Process exec(String cmd, String[] envp, String dir) throws IOException {

        if (dir != null && !"".equals(dir.trim())) {
            return runtime.exec(cmd, envp, new File(dir));
        } else {
            return exec(cmd, envp);
        }
    }

    /**
    * Executes the specified command and arguments in a separate process with
    * the specified environment and working directory.
    * 
    * @param cmdarray array containing the command to call and its arguments
    * @param envp array of strings, each element of which has environment
    *    variable  settings in format name=value.
    * @param dir the working directory of the subprocess
    * @return Process a Process object for managing the external process
    * @throws IOException if an I/O error occurs.
    */
    public Process exec(String cmdarray[], String[] envp, String dir) throws IOException {
        if (dir != null && !"".equals(dir.trim())) {
            return runtime.exec(cmdarray, envp, new File(dir));
        } else {
            return exec(cmdarray, envp);
        }
    }
}
