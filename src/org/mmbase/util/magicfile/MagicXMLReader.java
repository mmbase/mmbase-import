package org.mmbase.util.magicfile;

import java.io.File;
import java.util.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.Element;

/**
 * Reads <config>/magic.xml
 */
public class MagicXMLReader extends XMLBasicReader implements DetectorProvider {

    private static Logger log = Logging.getLoggerInstance(MagicXMLReader.class);

    private static MagicXMLReader reader = null;
    protected static final String MAGICXMLFILE = "magic.xml";
    // Name of the XML magic file - should reside in top config dir

    private static FileWatcher watcher;

    private static void setReader(File file) throws IllegalArgumentException {
        if (!file.exists()) {
            throw new IllegalArgumentException("magic file  " + file + " does not exist");
        }
        reader = new MagicXMLReader(file.getAbsolutePath());
    }

    /**
     * Gets the one MagicXMLReader (there can only be one).
     * @return MagicXMLReader if mmbase was staterd or null if mmbase was not started
     */

    public synchronized static MagicXMLReader getInstance() {
        if (reader == null) { // can only occur once.
            String configPath = null;
            try {
                configPath = MMBaseContext.getConfigPath();
            } catch (RuntimeException e) {
                return null;
            }
            File magicxml = new File(configPath, MAGICXMLFILE);
            log.info("Magic XML file is: " + magicxml);
            try {
                setReader(magicxml);
            } catch (IllegalArgumentException e) {
                log.info("The file does not exist, cannot create MagicXMLReader instance");
                return null;
            }

            watcher = new FileWatcher(true) {
                protected void onChange(File file) {
                        // reader is replace on every change of magic.xml
    setReader(file);
                }
            };
            watcher.add(magicxml);
            watcher.start();

        }
        return reader;
    }
    private List detectors = null;

    private MagicXMLReader(String path) {
        super(path, MagicXMLReader.class);
    }

    public String getVersion() {
        Element e = getElementByPath("magic.info.version");
        return getElementValue(e);
    }
    public String getAuthor() {
        Element e = getElementByPath("magic.info.author");
        return getElementValue(e);
    }
    public String getDescription() {
        Element e = getElementByPath("magic.info.description");
        return getElementValue(e);
    }

    /**
     * Returns all 'Detectors'.
     */
    public List getDetectors() {
        if (detectors == null) {
            detectors = new Vector();
            Element e = getElementByPath("magic.detectorlist");
            if (e == null) {
                log.fatal("Could not find magic/detectorlist in magix.cml");
                // aargh!
                return detectors;
            }

            Enumeration enumeration = getChildElements(e);
            Detector d;
            while (enumeration.hasMoreElements()) {
                d = getOneDetector((Element)enumeration.nextElement());
                detectors.add(d);
            }
        }
        return detectors;
    }

    /**
     * Replaces octal representations of bytes, written as \ddd to actual byte values.
     */
    private String convertOctals(String s) {
        int p = 0;
        int stoppedAt = 0;
        StringBuffer buf = new StringBuffer();
        char c;
        while (p < s.length()) {
            c = s.charAt(p);
            if (c == '\\') {
                if (p > s.length() - 4) {
                    // Can't be a full octal representation here, let's cut it off
                    break;
                } else {
                    char c0;
                    boolean failed = false;
                    for (int p0 = p + 1; p0 < p + 4; p0++) {
                        c0 = s.charAt(p0);
                        if (!((int)c0 >= '0' && (int)c0 <= '9')) {
                            failed = true;
                        }
                    }
                    if (!failed) {
                        buf.append(s.substring(stoppedAt, p)).append((char)Integer.parseInt(s.substring(p + 1, p + 4), 8));
                        stoppedAt = p + 4;
                        p = p + 4;
                    } else {
                        p++;
                    }
                }
            } else {
                p++;
            }
        }
        buf.append(s.substring(stoppedAt, p));
        return buf.toString();
    }

    private Detector getOneDetector(Element e) {
        Detector d = new Detector();
        Element e1;

        e1 = getElementByPath(e, "detector.mimetype");
        d.setMimeType(getElementValue(e1));

        e1 = getElementByPath(e, "detector.extension");
        d.setExtension(getElementValue(e1));

        e1 = getElementByPath(e, "detector.designation");
        d.setDesignation(getElementValue(e1));

        e1 = getElementByPath(e, "detector.test");
        d.setTest(convertOctals(getElementValue(e1)));

        d.setOffset(getElementAttributeValue(e1, "offset"));
        d.setType(getElementAttributeValue(e1, "type"));
        String comparator = getElementAttributeValue(e1, "comparator");
        if (comparator.equals("&gt;")) {
            d.setComparator('>');
        } else if (comparator.equals("&lt;")) {
            d.setComparator('<');
        } else if (comparator.equals("&amp;")) {
            d.setComparator('&');
        } else if (comparator.length() == 1) {
            d.setComparator(comparator.charAt(0));
        } else {
            d.setComparator('=');
        }

        e1 = getElementByPath(e, "detector.childlist");
        if (e1 != null) {
            Enumeration enumeration = getChildElements(e1);
            Detector child;
            while (enumeration.hasMoreElements()) {
                e1 = (Element)enumeration.nextElement();
                child = getOneDetector(e1);
                d.addChild(child, 1); // Not sure if this is the right thing
            }
        }
        return d;
    }
}
