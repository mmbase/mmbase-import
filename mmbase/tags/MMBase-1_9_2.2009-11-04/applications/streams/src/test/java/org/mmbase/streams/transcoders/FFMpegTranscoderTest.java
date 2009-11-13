package org.mmbase.streams.transcoders;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.*;



/**
 * @author Michiel Meeuwissen
 */

public class FFMpegTranscoderTest {

    private static final org.mmbase.util.logging.Logger LOG = Logging.getLoggerInstance(FFMpegTranscoderTest.class);

    protected File getInput() {
        File baseDir = new File(System.getProperty("user.dir"));
        File samples = new File(baseDir, "samples");
        return new File(samples, "basic.mp4");
    }

    @Test
    public void key() {
        FFMpegTranscoder trans = new FFMpegTranscoder();
        assertNotNull(trans.getKey());
        assertEquals("FFMpegTranscoder format=AVI", trans.getKey());
    }

    @Test
    public void unkownEncoding() throws Exception {
        File input = getInput();
        assumeTrue(input.exists());

        File output = File.createTempFile("bla", ".mpeg");

        FFMpegTranscoder trans = new FFMpegTranscoder().clone();

        trans.setVcodec("UNKNOWN CODEC");
        org.mmbase.util.logging.Logger dummy = new WriterLogger(new OutputStreamWriter(System.err));
        dummy.setLevel(Level.FATAL);

        try {
            trans.transcode(input.toURI(), output.toURI(), dummy);
            fail("Should have thrown exception");
        } catch (Error e) {
            // OK
        }



    }

}


