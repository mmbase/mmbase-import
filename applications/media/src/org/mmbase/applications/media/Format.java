 /*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
 */

package org.mmbase.applications.media;

import java.util.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMBaseContext;
import org.w3c.dom.Element;


// import org.mmbase.util.ConstantsBundle;

/**
 * Makes the 'Format' constants available.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Format.java,v 1.24 2009-04-30 09:26:38 michiel Exp $
 * @since MMBase-1.7
 */
// See http://www.javaworld.com/javaworld/jw-07-1997/jw-07-enumerated.html
public final class Format {   // final class!!
    private static Logger log = Logging.getLoggerInstance(Format.class);

    public final static String RESOURCE = "org.mmbase.applications.media.resources.formats";
    public static final String PUBLIC_ID_MIMEMAPPING_1_0 = "-//MMBase//DTD mimemapping config 1.0//EN";
    public static final String DTD_MIMEMAPPING_1_0       = "mimemapping_1_0.dtd";


    // in case you want i18ed format strings.

    private static Map<String,String> mimeMapping = null;
    static {

        org.mmbase.util.xml.EntityResolver.registerPublicID(PUBLIC_ID_MIMEMAPPING_1_0, DTD_MIMEMAPPING_1_0, Format.class);

        String mimeMappingFile = "media/mimemapping.xml";
        readMimeMapping(mimeMappingFile);
        ResourceWatcher watcher = new ResourceWatcher() {
                public void onChange(String file) {
                    readMimeMapping(file);
                }
            };
        watcher.add(mimeMappingFile);
        watcher.start();

    }

    static void readMimeMapping(String mimeMappingFile) {
        mimeMapping = new HashMap<String, String>();


        log.service("Reading " + mimeMappingFile);
        try {
            DocumentReader reader = new DocumentReader(ResourceLoader.getConfigurationRoot().getDocument(mimeMappingFile, DocumentReader.validate(), Format.class));

            for(Element map:reader.getChildElements("mimemapping", "map")) {
                String format = reader.getElementAttributeValue(map, "format");
                String codec = reader.getElementAttributeValue(map, "codec");
                String mime = reader.getElementValue(map);

                mimeMapping.put(format + "/" + codec,mime);
                log.debug("Adding mime mapping " + format + "/" + codec + " -> " + mime);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private static List<Format> formats = new ArrayList<Format>(); // to make possible to get the Format object by int.
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
    public static final Format ASF  = new Format(12, "asf"); /* windows media */
    public static final Format MOV  = new Format(13, "mov");
    public static final Format WMA  = new Format(14, "wma"); /* windows media */
    public static final Format OGG  = new Format(15, "ocg");
    public static final Format OGM  = new Format(16, "ogm");
    public static final Format RAM  = new Format(17, "ram");
    public static final Format WMP  = new Format(18, "wmp"); /* windows media */
    public static final Format HTML  = new Format(19, "html");
    public static final Format SMIL  = new Format(20, "smil");
    public static final Format QT    = new Format(21, "qt");

    /* more windows media types */
    public static final Format ASX   = new Format(22, "asx");
    public static final Format WAX   = new Format(23, "wax");
    public static final Format WMV   = new Format(24, "wmv");
    public static final Format WVX   = new Format(25, "wvx");
    public static final Format WM    = new Format(26, "wm");
    public static final Format WMX   = new Format(27, "wmx");
    public static final Format WMZ   = new Format(28, "wmz");
    public static final Format WMD   = new Format(29, "wmd");

    public static final Format MID   = new Format(30, "mid");

    public static final Format PODCAST = new Format(50, "podcast");
    public static final Format VODCAST = new Format(51, "vodcast");

    public static final Format M4A = new Format(60, "m4a");
    public static final Format M4V = new Format(61, "m4v");

    public static final Format GGP = new Format(70, "3gpp");

    public static final Format FLASH = new Format(80, "flv");

    public int toInt()    { return number; }
    public String toString() { return id;     }
    public static Format get(int i) {
        try {
            return formats.get(i);
        } catch (java.lang.IndexOutOfBoundsException e) {
            return UNKNOWN;
        }
    }

    /**
     * don't know if this is nice
     */
    public static List<Format> getMediaFormats() {
        return Arrays.asList(new Format[] {MP3, RA, RA,WAV, PCM, MP2, RM, VOB, AVI, MPEG, MP4, MPG, ASF, MOV, WMA, OGG, OGM, RAM, WMP, QT, ASX, WAX, WMV, WVX, WM, WMZ, WMD, MID, PODCAST, VODCAST, M4A, M4V, GGP, FLASH});
    }
    public static Format get(String id) {
        id = id.toLowerCase();
        Iterator<Format> i = formats.iterator();
        while (i.hasNext()) {
            Format format = i.next();
            if(format.toString().equals(id)) {
                return format;
            }
        }
        log.warn("Unknown media format '" + id + "'. Returning " + UNKNOWN);
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


    protected static final List<Format> windowsMedia = Arrays.asList(new Format[] {ASF, WMP, WMA, ASX,  WAX, WMV, WVX, WM, WMX, WMZ, WMD});
    protected static final List<Format> real         = Arrays.asList(new Format[] {RA, RM, RAM});

    public boolean isReal() {
        return real.contains(this);
    }
    public boolean isWindowsMedia() {
        return windowsMedia.contains(this);
    }
    public List<Format> getSimilar() {
        if (isReal()) {
            return real;
        } else if (isWindowsMedia()) {
            return windowsMedia;
        }
        return Arrays.asList(new Format[]{this});
    }

    public String getMimeType() {
        return getMimeType(null);
    }

    public String getMimeType(String codec) {
        String format = toString();
        if(format == null || format.equals("unknown")) {
            format = "*";
        }
        if(codec == null || codec.equals("")) {
            codec = "*";
        }

        String mimeType = mimeMapping.get(format + "/" + codec);
        while (mimeType == null) {
            if (! codec.equals("*")) {
                mimeType = mimeMapping.get(format + "/*");
                if (mimeType != null) break;
            }
            if (! format.equals("*")) {
                mimeType = mimeMapping.get("*/" + codec);
                if (mimeType != null) break;
            }
            mimeType = mimeMapping.get("*/*");
            if (mimeType == null) mimeType =  "application/octet-stream";
            break;
        }

        if (log.isDebugEnabled()) {
            log.info("Finding mimetype for " + this + " -> " + mimeType + " (used " + mimeMapping + ")");
        }
        return mimeType;

    }

    public boolean equals(Object o) {
        if (o instanceof Format) {
            Format f = (Format) o;
            return f.number == number;
        }
        return false;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return number;
    }
}

