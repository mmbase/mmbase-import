package org.mmbase.streams.thumbnails;

import org.junit.Test;
import org.mmbase.util.logging.Level;
import org.mmbase.util.logging.Logging;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Michiel Meeuwissen
 */
public class FFMpegThumbNailCreatorTest {


    @Test
    public void call() throws Exception {
        Logging.getLoggerInstance(FFMpegThumbNailCreator.class).setLevel(Level.DEBUG);
        final File file =  new File("samples", "basic.mp4");
        System.out.println("file "  + file);
        FFMpegThumbNailCreator creator = new FFMpegThumbNailCreator() {
            @Override
            protected File getInput() {
                return file;

            }
            @Override
            protected void setOutput(File file) {
                assertTrue(file.length() > 0);
            }
            @Override
            protected double getTime() {
                return 9.5;
            }
            @Override
            protected File getTempDir() {
                return new File("/tmp");
            }
             @Override
            protected String getCommand() {
                return "/opt/local/bin/ffmpeg";
            }

        };
        assumeTrue(file.exists());
        assumeTrue(new File(creator.getCommand()).exists());
        assertEquals(Long.valueOf(131135), creator.call());

    }
}
