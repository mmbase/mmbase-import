/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.implementations.dropboxes;

import java.io.*;
import java.util.StringTokenizer;

import org.mmbase.service.interfaces.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author  vpro
 * @version  $Id: dropboxUnix.java,v 1.6 2002-04-29 10:54:17 pierre Exp $
 * @rename  DropboxUnix
 */

public class dropboxUnix implements dropboxInterface {

    private static Logger log = Logging.getLoggerInstance(dropboxUnix.class.getName());
    private String dir;
    private String cmd;
    private String wwwpath;

    public void startUp() {
    }

    public void shutDown() {
    }

    public String getVersion() {
        return ("0.31");
    }

    public String doDir(String cmds) {
        String result = "";
        String cmdline = cmd + " " + dir;
        log.debug("EXEC->" + cmdline);
        result = execute(cmdline);
        log.debug("EXEC->" + result);
        StringTokenizer tok = new StringTokenizer(result, "\n\r");
        String result2 = "";
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            log.debug("FILE=" + line);
            result2 += wwwpath + line + ",";
        }

        return result2;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setWWWPath(String wwwpath) {
        this.wwwpath = wwwpath;
    }

    /**
     * Executes the given command
     *
     * @param  command the command to execute
     * @return  the result of the command
     */
    private String execute(String command) {
        Process p = null;
        String result = "";
        String tmp = "";
        try {
            p = (Runtime.getRuntime()).exec(command, null);
        } catch (Exception e) {
            log.error(e.toString());
            return e.toString();
        }
        BufferedReader dip = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            while ((tmp = dip.readLine()) != null) {
                result += tmp + "\n";
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return result;
    }
}

