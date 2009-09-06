package org.mmbase.streams.transcoders;

import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * Low level tests of Regexps and stuff.
 * @author Michiel Meeuwissen
 */

public class AnalyzerUtilsTest {


    Transcoder getFFMpegTranscoder() {
        FFMpegRecognizer rec = new FFMpegRecognizer();
        Transcoder trans = new RecognizerTranscoder(rec);
        return trans;
    }


    @Test
    public void duration() throws Exception {
        File testFile = new File(System.getProperty("user.dir"), "samples" + File.separator + "basic.mp4");
        assumeTrue(testFile.exists());

        Logger test = new Logger() {
                @Override
                 protected void log(String s) {
                    if (util.duration(s, source, destination)) {
                        this.success = true;
                    }
                }
            };
        getFFMpegTranscoder().transcode(testFile.toURI(), null, test);
        assertTrue(test.success);

    }

    @Test
    public void unknown() throws Exception {
        File testFile = new File(System.getProperty("user.dir"), "samples" + File.separator + "unknown.wav");
        assumeTrue(testFile.exists());

        Logger test = new Logger() {
                @Override
                protected void log(String s) {
                    if (util.unsupported(s, source, destination)) {
                        this.success = true;
                    }
                }
            };
        getFFMpegTranscoder().transcode(testFile.toURI(), null, test);
        assertTrue(test.success);

    }

    @Test
    public void known() throws Exception {
        File testFile = new File(System.getProperty("user.dir"), "samples" + File.separator + "basic.wav");
        assumeTrue(testFile.exists());

        Logger test = new Logger() {
                @Override
                protected void log(String s) {
                    if (util.unsupported(s, source, destination)) {
                        this.success = true;
                    }
                }
            };
        getFFMpegTranscoder().transcode(testFile.toURI(), null, test);
        assertFalse(test.success);

    }


    @Test
    public void unsupported() throws Exception {
        File testFile = new File(System.getProperty("user.dir"), "samples" + File.separator + "unsupported.rm");
        assumeTrue(testFile.exists());

        Logger test = new Logger() {
                @Override
                protected void log(String s) {
                    if (util.unsupported(s, source, destination)) {
                        this.success = true;
                    }
                }
            };
        getFFMpegTranscoder().transcode(testFile.toURI(), null, test);
        assertTrue(test.success);

    }


}


