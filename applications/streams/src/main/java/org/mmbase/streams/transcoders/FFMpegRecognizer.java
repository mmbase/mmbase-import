/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.externalprocess.*;
import org.mmbase.util.WriterOutputStream;
import java.util.regex.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: FFMpegTranscoder.java 36518 2009-07-02 12:52:01Z michiel $
 */
public class FFMpegRecognizer implements Recognizer {

    private static final Logger log = Logging.getLoggerInstance(FFMpegRecognizer.class);

    private MimeType mimeType;

    public MimeType getMimeType() {
        return mimeType;
    }
    public void setMimeType(String s) {
        mimeType = new MimeType(s);
    }

    public void analyze(URI in, Logger logger) throws Exception {
        Writer writer = new LoggerWriter(logger, Level.SERVICE);
        OutputStream outStream = new WriterOutputStream(writer, System.getProperty("file.encoding"));
        //log.service("Calling (" + method + ") " + getCommand() + " " + Arrays.asList(getArguments()));
        File inFile = new File(in.getPath());
        CommandExecutor.execute(outStream, new CommandExecutor.Method(), "ffmpeg", new String[] {
                "-i", inFile.toString(),
            }
            );
    }


    public FFMpegRecognizer clone() {
        try {
            return (FFMpegRecognizer) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // I hate java
            return null;
        }
    }

    public String toString() {
        return getClass().getName();
    }


    public static void main(String[] argv) throws Exception {
        Logger logger = Logging.getLoggerInstance("RECOGNIZER");
        logger.setLevel(Level.WARN);
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
}
