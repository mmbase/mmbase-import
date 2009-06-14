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
 * Makes the 'Format' constants available.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
// See http://www.javaworld.com/javaworld/jw-07-1997/jw-07-enumerated.html
public final class Codec {   // final class!!
    private static Logger log = Logging.getLoggerInstance(Codec.class);

    public final static String RESOURCE = "org.mmbase.applications.media.resources.codecs";
    // in case you want i18ed format strings.

    private static List<Codec> codecs = new ArrayList<Codec>(); // to make possible to get the Codec object by int.
    private int    number; // for storage
    private String id;     // for toString(), and as identifier in config file etc.
                           // Also sync with common extension?
                           // perhaps this could as well be used for storage


    private Codec(int n, String i) { // private constructor!!
        number = n; id = i;
        if (n >= 0) {
            while (codecs.size() <= number) {
                codecs.add(null);
            }
            codecs.set(number, this);
        }
    }


    // Codecs
    public final static Codec UNKNOWN = new Codec(0, "unknown");
    public final static Codec VORBIS  = new Codec(1, "vorbis");
    public final static Codec G2      = new Codec(2, "gd");
    public final static Codec DIV3    = new Codec(3, "div3");
    public final static Codec DIV4    = new Codec(4, "div4");
    public final static Codec DIVX    = new Codec(5, "divx");
    public final static Codec MP1     = new Codec(6, "mp1");
    public final static Codec MP2     = new Codec(7, "mp2");
    public final static Codec MP3     = new Codec(8, "mp3");
    public final static Codec MP4     = new Codec(9, "mp4");

    // in contradiction to the example of the cited URL I prefer
    // to state the number explicitely, because those numbers will
    // appear in the database, so never may change (so don't
    // determin the number automaticly


    public int toInt()    { return number; }
    @Override
    public String toString() { return id;     }
    public static Codec get(int i) {
        if (i < 0) return UNKNOWN;
        try {
            return codecs.get(i);
        } catch (java.lang.IndexOutOfBoundsException e) {
            return UNKNOWN;
        }
    }

    public static Codec get(String id) {
        id = id.toLowerCase();
        Iterator<Codec> i = codecs.iterator();
        while (i.hasNext()) {
            Codec codec = i.next();
            if(codec.toString().equals(id)) return codec;
        }
        log.error("Cannot convert codec (" + id + ") to number");
        return UNKNOWN;
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
    @Override
    public boolean equals(Object o) {
        if (o instanceof Codec) {
            Codec c = (Codec) o;
            return c.number == number;
        }
        return false;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return number;
    }
}

