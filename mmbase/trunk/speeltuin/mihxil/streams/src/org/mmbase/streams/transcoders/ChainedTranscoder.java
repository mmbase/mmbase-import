/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
import java.util.*;

/**
 * A trancoder base on an external command, like <code>ffmpeg</code> or <code>ffmpeg2theora</code>
 *
 * @author Michiel Meeuwissen
 */

public class ChainedTranscoder implements Transcoder {


    private final List<Transcoder> list = new ArrayList<Transcoder>();

    {
        add(new FFMpeg2TheoraTranscoder());
    }

    public void add(Transcoder t) {
        list.add(t);
    }


    public void transcode(final URI in, final URI out) throws Exception {
        for (Transcoder t : list) {
            t.transcode(in, out);
        }
    }

}
