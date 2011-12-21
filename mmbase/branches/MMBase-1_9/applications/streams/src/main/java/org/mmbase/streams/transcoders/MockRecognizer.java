package org.mmbase.streams.transcoders;

import java.net.URI;

import org.mmbase.util.MimeType;
import org.mmbase.util.logging.Logger;

/**
 * @author Michiel Meeuwissen
 */
public class MockRecognizer implements Recognizer {
    private MimeType mimeType = new MimeType("video", "mp4");

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(String s) {
        mimeType = new MimeType(s);
    }

    public void analyze(URI in, Logger logger) throws Exception {
        System.out.println("hoi");
    }

    public Recognizer clone() {
        MockRecognizer req = new MockRecognizer();
        req.setMimeType(mimeType.toString());
        return req;

    }
}
