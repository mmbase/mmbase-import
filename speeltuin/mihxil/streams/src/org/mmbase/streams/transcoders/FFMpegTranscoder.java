/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
import java.io.*;
import org.mmbase.util.logging.*;


/**
 *
 * @author Michiel Meeuwissen
 */

public class FFMpegTranscoder extends CommandTranscoder {

    @Override
    protected  String getCommand() {
        return "ffmpeg";
    }

    @Override
    protected String[] getArguments(URI in, URI out) {
        if (! in.getScheme().equals("file")) throw new UnsupportedOperationException();
        if (! out.getScheme().equals("file")) throw new UnsupportedOperationException();

        File inFile = new File(in.getPath());
        File outFile = new File(out.getPath());

        return new String[] { "-i", inFile.toString(), outFile.toString() };
    }

    @Override
    public String  getExtension() {
        return "avi";
    }


    public static void main(String[] argv) throws Exception {
        CommandTranscoder transcoder = new FFMpeg2TheoraTranscoder();
        Logger logger = Logging.getLoggerInstance("FFMPEGTHEORA");
        logger.setLevel(Level.SERVICE);
        transcoder.transcode(new File(argv[0]).toURI(), new File(argv[1]).toURI(), logger);
    }
}
