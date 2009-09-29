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
    MP1(6),
    MP2(7),
    MP3(8),
    MP4(9),
    THEORA(10),

    /* audio */
    AAC(21),
    COOK(22),     // Real
    DIRAC(23),
    FLAC(24),
    SAMR(25),
    SPEEX(26),
    WAV(27),
    WMAV1(28),    // Windows Media Audio 1
    WMAV2(29),    // Windows Media Audio 2

    /* video */
    FLV(101),
    H263(102),
    H264(103),
    MPEGVIDEO(104),
    MPEG1VIDEO(105),
    RV20(106),    // RealVideo 2
    RV30(107),    // RealVideo 3
    RV40(108),    // RealVideo 4
    MPEG4(109),
    WMV1(110),    // Windows Media Video 7
    WMV2(111),    // Windows Media Video 8
    WMV3(112),    // Windows Media Video 9

    /* libs used in ffmpeg */
    LIBFAAC(201),
    LIBFAAD(202),
    LIBMP3LAME(203),
    LIBTHEORA(204),
    LIBVORBIS(205),
    LIBX264(206),
    LIBXVID(207),
    MPEG2VIDEO(208);

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

