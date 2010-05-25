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
 * Class for running programs and executing commands.
 * The methods in this class catch and return output from these commands (both info and error messages).
 *
 * @application VWMS
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 * @deprecated Use {@link org.mmbase.util.externalprocess.CommandExecutor}
 */
public class Execute {

    private static Logger log = Logging.getLoggerInstance(Execute.class.getName());

    /**
     * Executes a command or program.
     * The output of the program is returned as a string.
     * @param command An array of strings, in which teh first argument is the command to execute,
     *                and the rest its parameters
     * @return the command output
     */
    public String execute (String command[])  {
        Process p=null;
        String s="",tmp="";

        BufferedReader dip= null;
        BufferedReader dep= null;

        try {
            p = (Runtime.getRuntime()).exec(command,null);
            p.waitFor();
        } catch (Exception e) {
            s+=e.toString();
            return s;
        }

        dip = new BufferedReader( new InputStreamReader(p.getInputStream()));
        dep = new BufferedReader( new InputStreamReader(p.getErrorStream()));

        try {
            while ((tmp = dip.readLine()) != null) {
                   s+=tmp+"\n";
            }
            while ((tmp = dep.readLine()) != null) {
                s+=tmp+"\n";
            }
        } catch (Exception e) {
            return s;
        }
        return s;
    }

    /**
     * Executes a command or program.
     * The output of the program is returned as a string.
     * @param command the command to execute
     * @return the command output
     */
    public String execute (String command) {
        Process p=null;
        String s="",tmp="";

        BufferedReader dip= null;
        BufferedReader dep= null;

        try {
            p = (Runtime.getRuntime()).exec(command,null);
            p.waitFor();
        } catch (Exception e) {
            s+=e.toString();
            return s;
        }

        dip = new BufferedReader( new InputStreamReader(p.getInputStream()));
        dep = new BufferedReader( new InputStreamReader(p.getErrorStream()));

        try {
            while ((tmp = dip.readLine()) != null) {
                   s+=tmp+"\n";
            }
            while ((tmp = dep.readLine()) != null) {
                s+=tmp+"\n";
            }
        } catch (Exception e) {
            return s;
        }
        return s;
    }

    /**
     * Entry for direct invocation from the commandline.
     * Usage:<br />
     * java Execute [command]<br/>
     * Does not take parameters.
     * @deprecated Only for testing. I mean, why bother?
     */
    public static void main(String args[]) {
        Execute execute = new Execute();
        log.info(execute.execute(args[0]));
    }
}
