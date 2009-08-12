package org.mmbase.streams;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.mmbase.bridge.virtual.*;
import org.mmbase.streams.transcoders.*;
import org.mmbase.util.logging.*;

/**
 * @author Michiel Meeuwissen
 */

public class Test {

    private final static CloudContext cloudContext = VirtualCloudContext.getCloudContext();

    private final static Map<String, File> files = new HashMap<String, File>();

    private static class Case {
        final String file;
        final String sourceType;
        final String destType;
        Case(String f, String e) {
            file = f;
            sourceType = e;
            destType = sourceType + "caches";
        }
    }
    static Case[] CASES = new Case[] {
        new Case("basic.mpg", AnalyzerUtils.VIDEO),
        new Case("basic.mp3", AnalyzerUtils.AUDIO),
        new Case("basic.png", AnalyzerUtils.IMAGE)
    };

    @BeforeClass
    public static void setUp() {
        {
            Map<String, DataType> undef = new HashMap<String, DataType>();
            undef.put("number", Constants.DATATYPE_INTEGER);
            undef.put("mimetype", Constants.DATATYPE_STRING);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.MEDIA, undef);
        }
        {
            Map<String, DataType> images = new HashMap<String, DataType>();
            images.put("number", Constants.DATATYPE_INTEGER);
            images.put("height", Constants.DATATYPE_INTEGER);
            images.put("width", Constants.DATATYPE_INTEGER);
            images.put("mimetype", Constants.DATATYPE_STRING);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.IMAGE, images);
        }
        {
            Map<String, DataType> audio = new HashMap<String, DataType>();
            audio.put("number", Constants.DATATYPE_INTEGER);
            audio.put("bitrate", Constants.DATATYPE_INTEGER);
            audio.put("mimetype", Constants.DATATYPE_STRING);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.AUDIO, audio);
        }
        {
            Map<String, DataType> video = new HashMap<String, DataType>();
            video.put("number", Constants.DATATYPE_INTEGER);
            video.put("height", Constants.DATATYPE_INTEGER);
            video.put("width", Constants.DATATYPE_INTEGER);
            video.put("bitrate", Constants.DATATYPE_INTEGER);
            video.put("mimetype", Constants.DATATYPE_STRING);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.VIDEO, video);
        }
        File baseDir = new File(System.getProperty("user.dir"));
        File samples = new File(baseDir, "samples");
        for (File sample : samples.listFiles()) {
            files.put(sample.getName(), sample);
        }
        System.out.println("" + files);
    }


    Node getTestNode() {
        Node n = cloudContext.getCloud("mmbase").getNodeManager(AnalyzerUtils.MEDIA).createNode();
        n.commit();
        return n;
    }


    @org.junit.Test
    public void testRecognizer()  throws Exception {
        CommandTranscoder transcoder = new FFMpegTranscoder("1").clone();
        Logger logger = Logging.getLoggerInstance("FFMPEG");

        ChainedLogger chain = new ChainedLogger(logger);
        Analyzer a = new FFMpegAnalyzer();


        chain.setLevel(Level.WARN);

        for (Case c : CASES) {
            Node source = getTestNode();
            Node dest   = getTestNode();
            AnalyzerLogger an = new AnalyzerLogger(a, source, dest);
            chain.addLogger(an);
            File f = files.get(c.file);
            if (f == null || ! f.exists()) {
                throw new Error("The file " + c.file  + " does not exist. Please download these first (use the Makefile)");
            }
            transcoder.transcode(f.toURI(), null, chain);
            a.ready(source, dest);
            chain.removeLogger(an);

            assertEquals(source.getNodeManager().getName(), c.sourceType);
            assertEquals(dest.getNodeManager().getName(), c.destType);

            System.out.println("" + source + " -> " + dest);

        }



    }

    @org.junit.Test
    public void anotherTest() {

    }

}


