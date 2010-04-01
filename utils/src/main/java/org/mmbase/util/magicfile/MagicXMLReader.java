package org.mmbase.util.magicfile;

import java.io.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.xml.Instantiator;
import org.mmbase.util.xml.XMLWriter;
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
    public List<Detector> getDetectors()  {
        if (detectors == null) {
            detectors = new CopyOnWriteArrayList<Detector>();
            Element e = getElementByPath("magic.detectorlist");
            if (e == null) {
                log.fatal("Could not find magic/detectorlist in magic.xml");
                // aargh!
                return detectors;
            }
            for (Element element : getChildElements(e)) {
                try {
                    Detector d = getOneDetector(element);
                    detectors.add(d);
                } catch (Exception ex) {
                    log.error(ex.getClass() + " " + ex.getMessage() + ": " + XMLWriter.write(element));
                }
            }
        }

        return detectors;
    }

    private Detector getOneDetector(Element e) throws
        org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
        java.lang.reflect.InvocationTargetException {
        Detector d = (Detector) Instantiator.getInstance(e);


        d.configure(e);

        Element e1 = getElementByPath(e, "detector.childlist");
        if (e1 != null) {
            for (Element element: getChildElements(e1)) {
                try {
                    Detector child = getOneDetector(element);
                    d.addChild(child, 1); // Not sure if this is the  thing
                } catch (Exception ex) {
                    log.warn(ex.getClass() + " " + ex.getMessage() + ": " + XMLWriter.write(e1));
                }
            }
        }
        return d;
    }
}
