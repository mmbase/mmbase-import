package org.mmbase.streams;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;
import static org.mmbase.datatypes.Constants.*;
import org.mmbase.bridge.dummy.*;
import org.mmbase.streams.transcoders.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;
import org.mmbase.util.logging.*;



/**
 * @author Michiel Meeuwissen
 */

@RunWith(Parameterized.class)
public class RecognizerTest {

    private final static CloudContext cloudContext = DummyCloudContext.getInstance();

    private final static Map<String, File> files = new HashMap<String, File>();

    private static class Case {
        final String file;
        final String sourceType;
        final String destType;
        final int    sourceHeight;
        final int    sourceWidth;
        Case(String f, String e, int x, int y) {
            file = f;
            sourceType = e;
            if (!sourceType.equals(IMAGE)) {
                destType = sourceType + "caches";
            } else {
                destType = null;
            }
            sourceHeight = y;
            sourceWidth  = x;
        }
        public String toString() {
            return sourceType + ":" + file;
        }
    }
    private final Case c;
    public RecognizerTest(Case c) {
        this.c = c;
    }
    static File samples;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
	
        return  Arrays.asList(new Object[][] {
                {new Case("basic.mpg", VIDEO, 320, 240)}
                ,
                {new Case("basic.mov", VIDEO, 640, 480)}
                ,
                {new Case("basic.mp3", AUDIO, -1, -1)}
                ,
                {new Case("basic.png", IMAGE, 88, 31)}
                ,
                {new Case("basic.flv", VIDEO, 480, 360)}
                ,
                {new Case("basic.jpg", IMAGE, 218, 218)}
        });
    }

    private static Map<String, DataType> getParent() {
        Map<String, DataType> undef = new LinkedHashMap<String, DataType>();
        undef.put("number", DATATYPE_INTEGER);
        undef.put("mimetype", DATATYPE_STRING);
        undef.put("mediafragment", DATATYPE_NODE);
        undef.put("url", DATATYPE_STRING);
        return undef;
    }

    @BeforeClass
    public static void setUp() {
        {
            Map<String, DataType> undef = getParent();
            DummyCloudContext.getInstance().addNodeManager(MEDIA, undef);
        }
        {
            Map<String, DataType> images = getParent();
            images.put("height", DATATYPE_INTEGER);
            images.put("width", DATATYPE_INTEGER);

            DummyCloudContext.getInstance().addNodeManager(IMAGE, images);
            DummyCloudContext.getInstance().addNodeManager(IMAGEC,images);
        }
        {
            Map<String, DataType> audio = getParent();
            audio.put("bitrate", DATATYPE_INTEGER);
            audio.put("bitrate", DATATYPE_INTEGER);
            audio.put("length", DATATYPE_INTEGER);

            DummyCloudContext.getInstance().addNodeManager(AUDIO, audio);
            DummyCloudContext.getInstance().addNodeManager(AUDIOC, audio);
        }
        {
            Map<String, DataType> video = getParent();
            video.put("height", DATATYPE_INTEGER);
            video.put("width", DATATYPE_INTEGER);
            video.put("bitrate", DATATYPE_INTEGER);
            video.put("channels", DATATYPE_INTEGER);
            video.put("length", DATATYPE_INTEGER);

            DummyCloudContext.getInstance().addNodeManager(VIDEO, video);
            DummyCloudContext.getInstance().addNodeManager(VIDEOC, video);
        }
        File baseDir = new File(System.getProperty("user.dir"));
        samples = new File(baseDir, "samples");
	if (samples.exists()) {
	    for (File sample : samples.listFiles()) {
		files.put(sample.getName(), sample);
	    }
	} else {
	    System.err.println("No " + samples + " found, tests will not be done");

	}
    }


    Node getTestNode() {
        Node n = cloudContext.getCloud("mmbase").getNodeManager(MEDIA).createNode();
        n.commit();
        return n;
    }


    @Test
    public void test()  throws Exception {
	assumeTrue(samples.exists());
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
        if (source.getNodeManager().hasField("height")) {
            assertEquals(c.toString(), c.sourceHeight, source.getIntValue("height"));
            assertEquals(c.toString(), c.sourceWidth, source.getIntValue("width"));
        }
        if (c.destType != null) {
            assertEquals(c.toString(), c.destType,   dest.getNodeManager().getName());
        }

        System.out.println("" + source + " -> " + dest);

    }

}


