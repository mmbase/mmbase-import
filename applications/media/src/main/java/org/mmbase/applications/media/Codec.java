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
    THEORA(10);

    private static Logger log = Logging.getLoggerInstance(Codec.class);

    public final static String RESOURCE = "org.mmbase.applications.media.resources.codecs";
    // in case you want i18ed format strings.

    private int    number; // for storage

    private Codec(int n) { // private constructor!!
        number = n;
    }

    public int toInt()    { return number; }

    public static Codec get(int i) {
        for (Codec c : Codec.values()) {
            if (c.number == i) return c;
        }
        return UNKNOWN;
    }

    public static Codec get(String id) {
        return Codec.valueOf(id.toUpperCase());
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

