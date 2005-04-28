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
 * @version $Id: Format.java,v 1.12 2003-11-13 18:20:43 michiel Exp $
 * @since MMBase-1.7
 */
// See http://www.javaworld.com/javaworld/jw-07-1997/jw-07-enumerated.html
public final class Format {   // final class!!
    private static Logger log = Logging.getLoggerInstance(Format.class);

    public final static String RESOURCE = "org.mmbase.applications.media.resources.formats";
    // in case you want i18ed format strings.
    
    private static List formats = new ArrayList(); // to make possible to get the Format object by int.
    private int    number; // for storage     
    private String id;     // for toString(), and as identifier in config file etc.
                           // Also sync with common extension?
                           // perhaps this could as well be used for storage
    
    
    private Format(int n, String i) { // private constructor!!
        number = n; id = i; 
        while (formats.size() <= number) formats.add(UNKNOWN);
        formats.set(number, this);
    }         
    
    // in contradiction to the example of the cited URL I prefer
    // to state the number explicitely, because those numbers will
    // appear in the database, so never may change (so don't
    // determin the number automaticly
    
    public static final Format UNKNOWN = new Format(0, "unknown");
    public static final Format MP3  = new Format(1, "mp3");
    public static final Format RA   = new Format(2, "ra");
    public static final Format WAV  = new Format(3, "wav");
    public static final Format PCM  = new Format(4, "pcm");
    public static final Format MP2  = new Format(5, "mp2");
    public static final Format RM   = new Format(6, "rm");
    public static final Format VOB  = new Format(7, "vob");
    public static final Format AVI  = new Format(8, "avi");
    public static final Format MPEG = new Format(9, "mpeg");
    public static final Format MP4  = new Format(10, "mp4");
    public static final Format MPG  = new Format(11, "mpg");
    public static final Format ASF  = new Format(12, "asf");
    public static final Format MOV  = new Format(13, "mov");
    public static final Format WMA  = new Format(14, "wma");
    public static final Format OGG  = new Format(15, "ocg");
    public static final Format OGM  = new Format(16, "ogm");
    public static final Format RAM  = new Format(17, "ram");
    public static final Format WMP  = new Format(18, "wmp");
    public static final Format HTML  = new Format(19, "html");
    public static final Format SMIL  = new Format(20, "smil");
    public static final Format QT    = new Format(21, "qt");
    public int toInt()    { return number; }
    public String toString() { return id;     }
    public static Format get(int i) {
        try {
            return (Format) formats.get(i);
        } catch (java.lang.IndexOutOfBoundsException e) {
            return UNKNOWN;
        }
    }

    /**
     * don't know if this is nice
     */
    public static List getMediaFormats() {
        return Arrays.asList(new Format[] {MP3, RA, RA,WAV, PCM, MP2, RM, VOB, AVI, MPEG, MP4, MPG, ASF, MOV, WMA, OGG, OGM, RAM, WMP, QT});
    }
    public static Format get(String id) {
        id = id.toLowerCase();
        Iterator i = formats.iterator();
        while (i.hasNext()) {
            Format format = (Format) i.next();
            if(format.toString().equals(id)) return format;
        }
        log.error("Cannot convert format (" + id + ") to number");
        return UNKNOWN;
    }
    
    public String getGUIIndicator(Locale locale) {
        try {
            ResourceBundle m = ResourceBundle.getBundle(RESOURCE, locale);
            return m.getString("" + number);
            // return  ConstantsBundle.get(RESOURCE, this.getClass(), number, locale);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }


    public boolean isReal() {
        return this == RA || this == RM || this == RAM;
    }
    public boolean isWindowsMedia() {
        return this == ASF || this == WMP;
    }
    public List getSimilar() {
        if (isReal()) {
            if (this == RM) {
                return Arrays.asList(new Format[] {this, RA, RAM});
            } else if (this == RA) {
                return Arrays.asList(new Format[] {this, RM, RAM});
            } else if (this == RAM) {
                return Arrays.asList(new Format[] {this, RM, RA});
            }
        } else if (isWindowsMedia()) {
            if (this == ASF) {
                return Arrays.asList(new Format[] {this, WMP});
            } else if (this == WMP) {
                return Arrays.asList(new Format[] {this, ASF});
            }
        }
        return Arrays.asList(new Format[]{this});
    }
    public boolean equals(Object o) {
        if (o instanceof Format) {
            Format f = (Format) o;
            return f.number == number;
        }
        return false;
    }
}
    
