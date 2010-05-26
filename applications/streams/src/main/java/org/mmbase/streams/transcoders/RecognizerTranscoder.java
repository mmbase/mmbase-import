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

import java.net.URI;

import org.mmbase.bridge.Node;
import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.Format;
import org.mmbase.util.MimeType;
import org.mmbase.util.logging.Logger;


/**
 * This thin wrapper just represents a 'Recognizer' as a Transcoder. This makes administration easier.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractTranscoder.java 36425 2009-06-25 18:26:28Z michiel $
 */
public class RecognizerTranscoder implements Transcoder {

    final Recognizer recognizer;
    public RecognizerTranscoder(Recognizer rec) {
        recognizer = rec;
    }

    protected boolean clone = false;

    protected URI in;
    protected URI out;

    protected Format format;

    protected Codec  codec = Codec.UNKNOWN;

    protected MimeType  mimeType = MimeType.ANY;

    public void init(Node d) {
        throw new UnsupportedOperationException();
    }
    
    public void setFormat(String f) {
        throw new UnsupportedOperationException();
    }

    public Format getFormat() {
        return null;
    }

    public void setCodec(String c) {
        throw new UnsupportedOperationException();
    }

    public Codec getCodec() {
        return null;
    }

    public MimeType getMimeType() {
        return recognizer.getMimeType();
    }
    public void setMimeType(String m) {
        recognizer.setMimeType(m);
    }
    public String getInId() {
        return null;
    }
    public void setInId(String i) {
        if (i != null) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * The key of recognizes must be null, that avoid that any subsequent caches are created
     */
    public  final String getKey() {
        return null;

    }

    public final void transcode(final URI in, final URI out, final Logger log) throws Exception {
        if (in == null) throw new IllegalArgumentException();
        this.in = in;
        recognizer.analyze(in, log);
    }

    public URI getIn() {
        return in;
    }
    public URI getOut() {
        return null;
    }

    public RecognizerTranscoder clone() {
        try {
            RecognizerTranscoder c =  (RecognizerTranscoder) super.clone();
            c.clone = true;
            return c;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    public String toString() {
        return recognizer.toString();
    }




}
