/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.mmbase.util.logging.*;
import org.mmbase.util.magicfile.*;

/**
 * Tries to determin the mime-type of a byte array (or a file).
 *
 * @author cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id: MagicFile.java,v 1.12 2003-10-15 07:30:27 keesj Exp $
 */
public class MagicFile {
    private static Logger log = Logging.getLoggerInstance(MagicFile.class);

    public static final String FAILED = "Failed to determine type";
    // application/octet-stream?

    protected static int BUFSIZE = 4598;
    // Read a string of maximally this length from the file
    // Is this garanteed to be big enough?

    private static MagicFile instance;

    protected DetectorProvider detectors;

    public static MagicFile getInstance() {
        if (instance == null) {
            instance = new MagicFile();
        }
        return instance;
    }

    /**
     */
    private MagicFile(DetectorProvider d) {
        detectors = d;
    }

    private MagicFile() {
        DetectorProvider d = MagicXMLReader.getInstance();
        // default, read from XML
        if (d == null) {        	
            d = new MagicParser();
        }
        detectors = d;
    }

    /**
     * Returns a list of detectors used by this MagicFile instance
     */

    public List getDetectors() {
        return detectors.getDetectors();
    }

    /*
     * @deprecated use getMimeType(File)
     */
    protected String test(String path) {
        try {
            return getMimeType(new File(path));
        } catch (IOException e) {
            return "File not found " + path;
        }
    }
    /**
     * @param path Location of file to be checked
     * @return Type of the file as determined by the magic file
     */
    protected String getMimeType(File file) throws IOException {
        byte[] lithmus = new byte[BUFSIZE];
        //log.debug("path = "+path);
        FileInputStream fir = new FileInputStream(file);
        int res = fir.read(lithmus, 0, BUFSIZE);
        log.debug("read " + res + "  bytes from " + file.getAbsolutePath());
        return getMimeType(lithmus);
    }

    /**
     * Tests the byte[] array for the mime type.
     *
     * @return The found mime-type or FAILED
     */
    public String getMimeType(byte[] input) {
        byte[] lithmus;

        if (input.length > BUFSIZE) {
            lithmus = new byte[BUFSIZE];
            System.arraycopy(input, 0, lithmus, 0, BUFSIZE);
            log.debug("getMimeType was called with big bytearray cutting to " + BUFSIZE + " bytes");
        } else {
            lithmus = input;
        }

        Iterator i = getDetectors().iterator();
        while (i.hasNext()) {
            Detector detector = (Detector)i.next();
            log.debug("Trying " + detector.getMimeType());
            if (detector != null && detector.test(lithmus)) {
                //return detector.getDesignation();
                return detector.getMimeType();
            }
        }
        return FAILED;
    }

    /**
     *
     */

    public String extensionToMimeType(String extension) {
        Iterator i = getDetectors().iterator();
        while (i.hasNext()) {
            Detector detector = (Detector)i.next();
            Iterator j = detector.getExtensions().iterator();
            while (j.hasNext()) {
                String ex = (String)j.next();
                if (ex.equalsIgnoreCase(extension)) {
                    return detector.getMimeType();
                }
            }
        }
        return FAILED;
    }

    public String getMimeType(byte[] data, String extension) {
        String result;
        result = getMimeType(data);
        if (result.equals(FAILED)) {
            result = extensionToMimeType(extension);
        }
        return result;
    }

    /**
     * e.g.: java -Dmmbase.config=/home/mmbase/mmbase-app/WEB-INF/config org.mmbase.util.MagicFile test.doc
     */
    public static void main(String[] argv) {
        MagicFile magicFile = MagicFile.getInstance();

        if (argv.length == 1) {
            try {
                // one argument possible: a file name. Return the mime-type
                log.info(magicFile.getMimeType(new File(argv[0])));
            } catch (IOException e) {
                log.info(argv[0] + " cannot be opened or read: " + e.toString());
            }
        } else {
            // show the known Detectors;
            Iterator i = magicFile.getDetectors().iterator();
            while (i.hasNext()) {
                Detector d = (Detector)i.next();
                log.info(d.toString());
            }
        }
    }
}
