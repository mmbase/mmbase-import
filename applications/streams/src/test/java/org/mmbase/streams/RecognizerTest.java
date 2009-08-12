package org.mmbase.streams;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
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

@RunWith(Parameterized.class)
public class RecognizerTest {

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
    private final Case c;
    public RecognizerTest(Case c) {
        this.c = c;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return  Arrays.asList(new Object[][] {
                {new Case("basic.mpg", AnalyzerUtils.VIDEO)},
                {new Case("basic.mp3", AnalyzerUtils.AUDIO)},
                {new Case("basic.png", AnalyzerUtils.IMAGE)}
        });
    }

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
            VirtualCloudContext.addNodeManager(AnalyzerUtils.AUDIOC, audio);
        }
        {
            Map<String, DataType> video = new HashMap<String, DataType>();
            video.put("number", Constants.DATATYPE_INTEGER);
            video.put("height", Constants.DATATYPE_INTEGER);
            video.put("width", Constants.DATATYPE_INTEGER);
            video.put("bitrate", Constants.DATATYPE_INTEGER);
            video.put("mimetype", Constants.DATATYPE_STRING);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.VIDEO, video);
            VirtualCloudContext.addNodeManager(AnalyzerUtils.VIDEOC, video);
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
    public void test()  throws Exception {
        CommandTranscoder transcoder = new FFMpegTranscoder("1").clone();
        Logger logger = Logging.getLoggerInstance("FFMPEG");

        ChainedLogger chain = new ChainedLogger(logger);
        Analyzer a = new FFMpegAnalyzer();


        chain.setLevel(Level.WARN);

        Node source = getTestNode();
        Node dest   = getTestNode();
        AnalyzerLogger an = new AnalyzerLogger(a, source, dest);
        chain.addLogger(an);
        File f = files.get(c.file);
        if (f == null || ! f.exists()) {
            throw new Error("The file " + c.file  + " does not exist. Please download these first (use the Makefile)");
        }
        File out = File.createTempFile(Test.class.getName(), null);
        transcoder.transcode(f.toURI(), out.toURI(), chain);
        a.ready(source, dest);
        chain.removeLogger(an);

        assertEquals(source.getNodeManager().getName(), c.sourceType);
        assertEquals(dest.getNodeManager().getName(), c.destType);

        System.out.println("" + source + " -> " + dest);

    }

}


