 /*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
 */

package org.mmbase.applications.media;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import java.util.*;
// import org.mmbase.util.ConstantsBundle;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public enum Codec {
    UNKNOWN(-1),
    VORBIS(0),
    G2(2),
    DIV3(3),
    DIV4(4),
    DIVX(5),
    MP1(6),     // should be MP1 (MPEG audio layer 1)
    MP2(7),     // should be MP2 (MPEG audio layer 2)
    MP3(8),     // should be MP3 (MPEG audio layer 3)
    MP4(9),     // same as MPEG4 ?
    THEORA(10),

    /* audio */
    AAC(21),
    COOK(22),     // common RealAudio codec
    DIRAC(23),
    FLAC(24),
    SAMR(25),
    SPEEX(26),
    WAV(27),
    WMAV1(28),    // Windows Media Audio 1
    WMAV2(29),    // Windows Media Audio 2
    QDMC(30),     // QDesign Music Codec (mostly found in mov)
    QDM2(31),

    /* video */
    FLV(101),
    H263(102),
    H264(103),
    MPEG(104),    // MPEG-1 video
    MPEG2(105),   // MPEG-2 video
    MPEG4(106),   // MPEG-4 video part 2
    RV20(107),    // RealVideo 2
    RV30(108),    // RealVideo 3
    RV40(109),    // RealVideo 4
    WMV1(110),    // Windows Media Video 7
    WMV2(111),    // Windows Media Video 8
    WMV3(112),    // Windows Media Video 9
    XVID(113),
    VP8(114),
    SVQ3(115);    // Sorenson Video 3 codec (found in QuickTime and Flash ("allmost h.263"))
    
    private static Logger log = Logging.getLoggerInstance(Codec.class);

    public final static String RESOURCE = "org.mmbase.applications.media.resources.codecs";
    // in case you want i18ed format strings.

    private int    number; // for storage

    private Codec(int n) { // private constructor!!
        number = n;
    }

    public int toInt()    {
        return number;
    }
    public int getValue() {
        return number;
    }

    public static Codec get(int i) {
        for (Codec c : Codec.values()) {
            if (c.number == i) return c;
        }
        return UNKNOWN;
    }

    public static Codec get(String id) {
        try {
            return Codec.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException iae) {
            log.error("IllegalArgument '" + id + "' : " + iae);
            return UNKNOWN;
        }

    }

    public String getGUIIndicator(Locale locale) {
        try {
            ResourceBundle m = ResourceBundle.getBundle(RESOURCE, locale);
            return m.getString("" + number);
            //return  ConstantsBundle.get(RESOURCE, this.getClass(), number, locale);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

}

