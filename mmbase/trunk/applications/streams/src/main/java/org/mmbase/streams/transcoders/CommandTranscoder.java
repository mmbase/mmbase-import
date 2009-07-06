/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.externalprocess.*;
import org.mmbase.util.WriterOutputStream;

import org.mmbase.util.logging.*;


/**
 * A trancoder base on an external command, like <code>ffmpeg</code> or <code>ffmpeg2theora</code>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public abstract class CommandTranscoder extends AbstractTranscoder {

    private CommandExecutor.Method method = new CommandExecutor.Method();

    private String path = "";

    public CommandTranscoder(String id) {
        super(id);
    }

    public void setMethod(CommandExecutor.Method m) {
        method = m;
    }

    public void setPath(String p) {
        path = p;
    }

    protected abstract String getCommand();

    protected String[] getEnvironment() {
        return new String[0];
    }
    protected abstract String[] getArguments();

    protected LoggerWriter getOutputWriter(Logger log) {
        return new LoggerWriter(log, Level.SERVICE);
    }

    protected void transcode(final Logger log) throws Exception {
        OutputStream outStream = new WriterOutputStream(getOutputWriter(log), System.getProperty("file.encoding"));
        log.service("Calling (" + method + ") " + getCommand() + " " + Arrays.asList(getArguments()));
        CommandExecutor.execute(outStream, method, path + getCommand(), getArguments());
    }


    public CommandTranscoder clone() {
        return  (CommandTranscoder) super.clone();
    }



}
