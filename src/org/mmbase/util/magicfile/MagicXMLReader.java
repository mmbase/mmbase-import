package org.mmbase.util.magicfile;

import java.io.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Reads <config>/magic.xml
 */
public class MagicXMLReader extends DocumentReader implements DetectorProvider {

    private static Logger log = Logging.getLoggerInstance(MagicXMLReader.class);

    private static MagicXMLReader reader = null;
    protected static final String MAGICXMLFILE = "magic.xml";
    // Name of the XML magic file - should reside in top config dir

    private static void setReader(String config) throws IllegalArgumentException {
        try {
            InputSource is = ResourceLoader.getConfigurationRoot().getInputSource(config);
            if (is != null) {
                reader = new MagicXMLReader(is);
            }
        } catch (IOException ie) {
            log.warn(ie);
        }
    }

    /**
     * Gets the one MagicXMLReader (there can only be one).
     * @return MagicXMLReader if mmbase was staterd or null if mmbase was not started
     */

    public synchronized static MagicXMLReader getInstance() {
        if (reader == null) { // can only occur once.

            setReader(MAGICXMLFILE);

            if (reader != null) {
                log.info("Magic XML file is: " + reader.getSystemId());
            }

            ResourceWatcher watcher = new ResourceWatcher() {
                    public void onChange(String file) {
                        // reader is replaced on every change of magic.xml
                        setReader(file);
                    }
                };
            watcher.start();
            watcher.add(MAGICXMLFILE);

        }
        return reader;
    }
    private List<Detector> detectors = null;

    private MagicXMLReader(InputSource is) {
        super(is, MagicXMLReader.class);
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
    public List<Detector> getDetectors() {
        if (detectors == null) {
            detectors = new CopyOnWriteArrayList<Detector>();
            Element e = getElementByPath("magic.detectorlist");
            if (e == null) {
                log.fatal("Could not find magic/detectorlist in magic.xml");
                // aargh!
                return detectors;
            }
            for (Element element : getChildElements(e)) {
                Detector d = getOneDetector(element);
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
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        char c;
        try {
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
                            if (!(c0 >= '0' && c0 <= '7')) {
                                failed = true;
                            }
                        }
                        if (!failed) {
                            byte[]  bytes = s.substring(stoppedAt, p).getBytes("US-ASCII");
                            buf.write(bytes, 0, bytes.length);
                            buf.write(Integer.parseInt(s.substring(p + 1, p + 4), 8));
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
            byte[]  bytes = s.substring(stoppedAt, p).getBytes("US-ASCII");
            buf.write(bytes, 0, bytes.length);
            return buf.toString("US-ASCII");
        } catch (java.io.UnsupportedEncodingException use) { // could not happen US-ASCII is supported
            return "";
        }
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
        if (e1 != null) {
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
        }

        e1 = getElementByPath(e, "detector.childlist");
        if (e1 != null) {
            for (Element element: getChildElements(e1)) {
                Detector child = getOneDetector(element);
                d.addChild(child, 1); // Not sure if this is the right thing
            }
        }
        return d;
    }
}
