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
import java.net.URI;

import org.mmbase.util.MimeType;

import org.mmbase.util.WriterOutputStream;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.util.logging.*;


/**
 * A recognizer that uses FFmpeg to analyze media.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class FFMpegRecognizer implements Recognizer {

    private static final Logger log = Logging.getLoggerInstance(FFMpegRecognizer.class);

    private String path = org.mmbase.util.ApplicationContextReader.getCachedProperties(getClass().getName()).get("path");

    private MimeType mimeType;

    public MimeType getMimeType() {
        return mimeType;
    }
    public void setMimeType(String s) {
        mimeType = new MimeType(s);
    }

    public void analyze(URI in, Logger logger) throws Exception {
        log.debug("Analyzing to " + logger);
        Writer writer = new LoggerWriter(logger, Level.SERVICE);
        OutputStream outStream = new WriterOutputStream(writer, System.getProperty("file.encoding"));
        OutputStream errStream = new WriterOutputStream(writer, System.getProperty("file.encoding"));
        String p = path;
        if (p == null) p = "";
        //log.service("Calling (" + method + ") " + getCommand() + " " + Arrays.asList(getArguments()));
        File inFile = new File(in.getPath());
        CommandExecutor.execute(outStream, errStream, new CommandExecutor.Method(), p + "ffmpeg", "-i", inFile.toString());
        outStream.close();
        errStream.close();
    }


    @Override
    public FFMpegRecognizer clone() {
        try {
            return (FFMpegRecognizer) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // I hate java
            return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

    /*
    public static void main(String[] argv) throws Exception {
        Logger logger = Logging.getLoggerInstance("RECOGNIZER");
        logger.setLevel(Level.DEBUG);
        Recognizer recognizer = new FFMpegRecognizer().clone();
        Analyzer a = new FFMpegAnalyzer();
        Node source = AnalyzerUtils.getTestNode();
        ChainedLogger chain = new ChainedLogger(logger);
        chain.addLogger(new AnalyzerLogger(a, source, null));
        recognizer.analyze(new File(argv[0]).toURI(), chain);
        a.ready(source, null);
        //chain.addLogger(new AnalyzerLogger(a, source, dest));
        //chain.setLevel(Level.DEBUG);
        System.out.println("" + source.getNodeManager() + " " + source);


    }
    */
}
