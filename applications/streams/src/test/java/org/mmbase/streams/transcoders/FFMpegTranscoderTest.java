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
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.externalprocess.ProcessException;
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
        } catch (ProcessException ie) {
            assumeTrue(false);
        }



    }

}


