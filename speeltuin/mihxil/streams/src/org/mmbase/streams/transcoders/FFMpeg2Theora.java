/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import java.util.*;
import org.mmbase.util.logging.*;
import javax.media.*;
import javax.media.format.*;

/**
 * A wrapper around the 'ffmpeg2theora' command line utility.
 *
 * @author Michiel Meeuwissen
 */

public class FFMpeg2Theora extends com.sun.media.BasicCodec implements javax.media.Codec {

    private static final Logger log = Logging.getLoggerInstance(FFMpeg2Theora.class);

    public String getName() {
        return "ffmpeg2theora";
    }


    public Format[]  getSupportedOutputFormats(Format input) {
        return new Format[] { new VideoFormat("ogg/theora") };
    }


    public int process(Buffer in, Buffer out)  {
        // call ffmpeg2theora command line?
        return BUFFER_PROCESSED_OK;
    }
}
