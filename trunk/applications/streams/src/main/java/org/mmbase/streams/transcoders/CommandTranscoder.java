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
    private static final Logger log = Logging.getLoggerInstance(CommandTranscoder.class);

    private CommandExecutor.Method method = new CommandExecutor.Method();

    private String path = org.mmbase.util.ApplicationContextReader.getCachedProperties(getClass().getName()).get("path");

    // TODO
    private Map<String, String> moreOptions = new HashMap<String, String>();

    public CommandTranscoder(String id) {
        super(id);
        log.service("" + getClass().getName() + " path:" + path);
    }

    public void setProperty(String key, String value) {
        moreOptions.put(key, value);
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
        String p = path;

        if (p == null) p = "";
        if (log.isServiceEnabled()) {
            log.service("Calling (" + method + ") " + p + getCommand() + " " + Arrays.asList(getArguments()));
        }

        // TODO Add support for 'moreOptions'
        // Here, but also in getKey.

        CommandExecutor.execute(outStream, method, p + getCommand(), getArguments());
    }


    public CommandTranscoder clone() {
        return  (CommandTranscoder) super.clone();
    }



}
