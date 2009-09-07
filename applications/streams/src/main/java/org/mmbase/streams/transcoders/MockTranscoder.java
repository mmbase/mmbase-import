/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.Format;
import java.net.*;
import java.io.*;

import org.mmbase.util.logging.*;


/**
 * This is a transcoder simply copies the file, and does not transcode anything. This is for testing only.
 *
 * @author Michiel Meeuwissen
 * @version $Id: InfiniteTranscoder.java 36518 2009-07-02 12:52:01Z michiel $
 */
@Settings({"format", "setting"})
public class MockTranscoder extends AbstractTranscoder {
    private static final Logger LOG = Logging.getLoggerInstance(MockTranscoder.class);
    private int seq = 0;

    protected boolean empty = false;
    protected String setting = "";

    public MockTranscoder() {
        format = Format.UNKNOWN;
    }

    public void setEmpty(boolean e) {
        empty = e;
    }
    public void setSetting(String s) {
        setting = s;
    }

    protected void transcode(final Logger log) throws Exception {
        log.info("Copying " + in + " to " + out);
        File outFile = new File(out.getPath());
        outFile.getParentFile().mkdirs();
        if (empty ) {
            org.mmbase.util.IOUtil.copy(new ByteArrayInputStream(new byte[0]),
                                        new FileOutputStream(new File(out.getPath())));

        } else {
            org.mmbase.util.IOUtil.copy(new FileInputStream(new File(in.getPath())),
                                        new FileOutputStream(new File(out.getPath())));
        }
    }
    @Override
    public MockTranscoder clone() {
        return (MockTranscoder) super.clone();
    }




}
