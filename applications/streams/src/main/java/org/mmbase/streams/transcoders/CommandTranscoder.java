/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams.transcoders;


import java.io.*;
import java.util.*;
import org.mmbase.util.externalprocess.*;
import org.mmbase.util.WriterOutputStream;

import org.mmbase.util.logging.*;


/**
 * A transcoder based on an external command, like <code>ffmpeg</code> or <code>ffmpeg2theora</code>. To call the
 * external command, {@link CommandExecutor} is used. This means that the command can run on the local machine, but also
 * on a remote machine. This is indicated by {@link #setMethod}, which is called for CommandExecutors when necessary by
 * {@link org.mmbase.streams.createcaches.Job} (and is based on settings in <code>createcaches.xml</code>).
 *
 * The command should be returns by {@link #getCommand}. The path of this command can be indicated by an appliction
 * context setting 'path' in the namespace named after the class or a super class.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public abstract class CommandTranscoder extends AbstractTranscoder {
    private static final Logger LOG = Logging.getLoggerInstance(CommandTranscoder.class);

    private CommandExecutor.Method method = new CommandExecutor.Method();

    private String path = null;
    {
        Class clazz = getClass();
        do {
            path =  org.mmbase.util.ApplicationContextReader.getCachedProperties(clazz.getName()).get("path");
            clazz = clazz.getSuperclass();
        } while (path == null && clazz != null);
    }

    private Map<String, String> moreOptions = new LinkedHashMap<String, String>();

    public CommandTranscoder() {
        LOG.service("" + getClass().getName() + " path:" + path);
    }

    public void setProperty(String key, String value) {
        moreOptions.put(key, value);
    }

    /**
     * Indicates how and 'where' the command must run.
     */
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

    protected LoggerWriter getErrorWriter(Logger log) {
        return new LoggerWriter(log, Level.ERROR);
    }

    /**
     * Overrides the generation of a key in {@link AbstractTranscoder} to add extra transcoding 
     * parameters that were not set by {@link Settings} annotations on the transcoders.
     */
    @Override
    public final String getKey() {
        StringBuilder key = new StringBuilder( super.getKey() );
        boolean appendedSetting = false;
        if (key.indexOf(", ") > 0) {
            appendedSetting = true;
        }
        
        for (Map.Entry<String, String> e : moreOptions.entrySet()) {
            if (appendedSetting) {
                key.append(", ");
            }
            key.append(e.getKey()).append("=").append(e.getValue());
            appendedSetting = true;
        }
        
        return key.toString();
    }
    
    @Override
    protected void transcode(final Logger log) throws Exception {
        OutputStream outStream = new WriterOutputStream(getOutputWriter(log), System.getProperty("file.encoding"));
        OutputStream errStream = new WriterOutputStream(getErrorWriter(log), System.getProperty("file.encoding"));
        String p = path;

        if (p == null) {
            p = "";
        }
        if (p.length() > 0 && ! p.endsWith(File.separator)) {
            p += File.separator;
        }

        List<String> args = new ArrayList<String>( Arrays.asList(getArguments()) );
        List<String> extra = new ArrayList<String>();
        for (Map.Entry<String, String> e : moreOptions.entrySet()) {
            extra.add(e.getKey());
            extra.add(e.getValue());
        }
        int pos = args.size() - 2; // last argument is outfile
        if (pos > -1) {
            if (!extra.isEmpty()) args.addAll(pos, extra); 
        } else {
            LOG.error("Not enough arguments, need at least in- and outfile.");
        }
        LOG.info("Calling (" + method + ") " + p + getCommand() + " " + args);
        CommandExecutor.execute(outStream, errStream, method, p + getCommand(), args.toArray(new String[args.size()]));
        outStream.close();
        errStream.close();
    }

    @Override
    public CommandTranscoder clone() {
        return  (CommandTranscoder) super.clone();
    }


}
