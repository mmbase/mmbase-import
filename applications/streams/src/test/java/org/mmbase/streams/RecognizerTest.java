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
            if (!sourceType.equals(AnalyzerUtils.IMAGE)) {
                destType = sourceType + "caches";
            } else {
                destType = null;
            }
        }
        public String toString() {
            return sourceType + ":" + file;
        }
    }
    private final Case c;
    public RecognizerTest(Case c) {
        this.c = c;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return  Arrays.asList(new Object[][] {
                {new Case("basic.mpg", AnalyzerUtils.VIDEO)}
                ,
                {new Case("basic.mp3", AnalyzerUtils.AUDIO)}
                ,
                {new Case("basic.png", AnalyzerUtils.IMAGE)}
        });
    }

    private static Map<String, DataType> getParent() {
        Map<String, DataType> undef = new LinkedHashMap<String, DataType>();
        undef.put("number", Constants.DATATYPE_INTEGER);
        undef.put("mimetype", Constants.DATATYPE_STRING);
        undef.put("mediafragment", Constants.DATATYPE_NODE);
        undef.put("url", Constants.DATATYPE_STRING);
        return undef;
    }

    @BeforeClass
    public static void setUp() {
        {
            Map<String, DataType> undef = getParent();
            VirtualCloudContext.addNodeManager(AnalyzerUtils.MEDIA, undef);
        }
        {
            Map<String, DataType> images = getParent();
            images.put("height", Constants.DATATYPE_INTEGER);
            images.put("width", Constants.DATATYPE_INTEGER);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.IMAGE, images);
            VirtualCloudContext.addNodeManager(AnalyzerUtils.IMAGEC,images);
        }
        {
            Map<String, DataType> audio = getParent();
            audio.put("bitrate", Constants.DATATYPE_INTEGER);
            audio.put("bitrate", Constants.DATATYPE_INTEGER);
            audio.put("length", Constants.DATATYPE_INTEGER);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.AUDIO, audio);
            VirtualCloudContext.addNodeManager(AnalyzerUtils.AUDIOC, audio);
        }
        {
            Map<String, DataType> video = getParent();
            video.put("height", Constants.DATATYPE_INTEGER);
            video.put("width", Constants.DATATYPE_INTEGER);
            video.put("bitrate", Constants.DATATYPE_INTEGER);
            video.put("channels", Constants.DATATYPE_INTEGER);
            video.put("length", Constants.DATATYPE_INTEGER);

            VirtualCloudContext.addNodeManager(AnalyzerUtils.VIDEO, video);
            VirtualCloudContext.addNodeManager(AnalyzerUtils.VIDEOC, video);
        }
        File baseDir = new File(System.getProperty("user.dir"));
        File samples = new File(baseDir, "samples");
        for (File sample : samples.listFiles()) {
            files.put(sample.getName(), sample);
        }
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

        assertEquals(c.toString(), c.sourceType, source.getNodeManager().getName());
        if (c.destType != null) {
            assertEquals(c.toString(), c.destType,   dest.getNodeManager().getName());
        }

        System.out.println("" + source + " -> " + dest);

    }

}


