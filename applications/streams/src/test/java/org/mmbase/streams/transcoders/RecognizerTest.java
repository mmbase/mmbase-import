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
import org.mmbase.bridge.mock.*;
import org.mmbase.streams.transcoders.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;
import org.mmbase.util.externalprocess.ProcessException;
import org.mmbase.util.logging.*;



/**
 * @author Michiel Meeuwissen
 */

@RunWith(Parameterized.class)
public class RecognizerTest {

    private final static CloudContext cloudContext = MockCloudContext.getInstance();

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
                //{new Case("basic2.mpg", VIDEO, 352, 288)}
                //,
                {new Case("basic.mov", VIDEO, 640, 480)}
                ,
                {new Case("basic.mp3", AUDIO, -1, -1)}
                ,
                {new Case("basic.mp4", VIDEO, 352, 288)}
                ,
                {new Case("basic.wav", AUDIO, -1, -1)}
                ,
                {new Case("basic.jpg", IMAGE, 218, 218)}
                ,
                {new Case("basic.png", IMAGE, 88, 31)}
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
        undef.put("state", DATATYPE_INTEGER);
        undef.put("codec", DATATYPE_INTEGER);
        return undef;
    }

    @BeforeClass
    public static void setUp() {
        {
            Map<String, DataType> undef = getParent();
            MockCloudContext.getInstance().addNodeManager(MEDIA, undef);
        }
        {
            Map<String, DataType> images = getParent();
            images.put("height", DATATYPE_INTEGER);
            images.put("width", DATATYPE_INTEGER);

            MockCloudContext.getInstance().addNodeManager(IMAGE, images);
            MockCloudContext.getInstance().addNodeManager(IMAGEC,images);
        }
        {
            Map<String, DataType> audio = getParent();
            audio.put("bitrate", DATATYPE_INTEGER);
            audio.put("bitrate", DATATYPE_INTEGER);
            audio.put("length", DATATYPE_INTEGER);

            MockCloudContext.getInstance().addNodeManager(AUDIO, audio);
            MockCloudContext.getInstance().addNodeManager(AUDIOC, audio);
        }
        {
            Map<String, DataType> video = getParent();
            video.put("height", DATATYPE_INTEGER);
            video.put("width", DATATYPE_INTEGER);
            video.put("bitrate", DATATYPE_INTEGER);
            video.put("channels", DATATYPE_INTEGER);
            video.put("length", DATATYPE_INTEGER);

            MockCloudContext.getInstance().addNodeManager(VIDEO, video);
            MockCloudContext.getInstance().addNodeManager(VIDEOC, video);
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

        for (int i = 0; i < 1; i++) {
            CommandTranscoder transcoder = new FFMpegTranscoder().clone();
            org.mmbase.util.logging.Logger logger = Logging.getLoggerInstance("FFMPEG");

            ChainedLogger chain = new ChainedLogger(logger);
            FFMpegAnalyzer a = new FFMpegAnalyzer();
            a.setUpdateSource(true);

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
            try {
                transcoder.transcode(f.toURI(), out.toURI(), chain);
            } catch (ProcessException e) {
                assumeTrue(false);
            }
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

}


