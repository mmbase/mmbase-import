/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.implementations.dropboxes;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.service.interfaces.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
  * @rename DropboxUnix
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
        return("0.31");
    }

      /**
      * executes the given command
      * @return standard output
      */
    private String execute (String command) {
        Process p=null;
        String s="",tmp="";
        DataInputStream dip= null;

        try {
            p = (Runtime.getRuntime()).exec(command,null);
        } catch (Exception e) {
            s+=e.toString();
            return s;
        }
        dip = new DataInputStream(p.getInputStream());
        try {
            while ((tmp = dip.readLine()) != null) {
                   s+=tmp+"\n"; 
           }
        } catch (Exception e) {
            //s+=e.toString();
            return s;
       }
       return s;
    }

    public String doDir( String cmds ) {
        String result = "";
        String cmdline=cmd+" "+dir;
        log.debug("EXEC->"+cmdline);
        result=execute(cmdline);
        log.debug("EXEC->"+result);
        StringTokenizer tok=new StringTokenizer(result,"\n\r");
        String result2="";
        while (tok.hasMoreTokens()) {
            String line=tok.nextToken();
            log.debug("FILE="+line);
            result2+=wwwpath+line+",";
        }

        return(result2);
    }


    public void setDir(String dir) {
        this.dir=dir;
    }

    public void setCmd(String cmd) {
        this.cmd=cmd;
    }

    public void setWWWPath(String wwwpath) {
        this.wwwpath=wwwpath;
    }
}
