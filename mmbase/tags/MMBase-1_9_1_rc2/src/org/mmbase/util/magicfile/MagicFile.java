/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.magicfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.mmbase.util.logging.*;

/**
 * Tries to determine the mime-type of a byte array (or a file).
 *
 * @author cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class MagicFile {
    private static final Logger log = Logging.getLoggerInstance(MagicFile.class);

    public static final String FAILED = "Failed to determine type";
    // application/octet-stream?

    protected static final int BUFSIZE = 4598;
    // Read a string of maximally this length from the file
    // Is this garanteed to be big enough?

    private static MagicFile instance;

    protected final DetectorProvider detectors;

    /**
     * Return the current instance of MagicFile. If no instance exists,
     * one is created.
     */
    public static MagicFile getInstance() {
        if (instance == null) {
            instance = new MagicFile();
        }
        return instance;
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

    public List<Detector> getDetectors() {
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
     * @param file Location of file to be checked
     * @return Type of the file as determined by the magic file
     */
    protected String getMimeType(File file) throws IOException {
        FileInputStream fir = null;
        try {
            byte[] lithmus = new byte[BUFSIZE];
            fir = new FileInputStream(file);
            int res = fir.read(lithmus, 0, BUFSIZE);
            log.debug("read " + res + "  bytes from " + file.getAbsolutePath());
            return getMimeType(lithmus);
        } catch (IOException ioe) {
            throw ioe;
        }
        finally {
            if (fir != null) {
                fir.close();
            }
        }
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

        List<Detector> list = getDetectors();
        if (list == null) {
            log.warn("No detectors found");
            return FAILED;
        }
        for (Detector detector : list) {
            if (detector.test(lithmus)) {
                log.debug("Trying " + detector.getMimeType());
                return detector.getMimeType();
            }
        }
        return FAILED;
    }

    /**
     * @javadoc
     */
    public String extensionToMimeType(String extension) {
        for (Detector detector : getDetectors()) {
            for (String ex : detector.getExtensions()) {
                if (ex.equalsIgnoreCase(extension)) {
                    return detector.getMimeType();
                }
            }
        }
        return FAILED;
    }

    /**
     * Given a mime-type string, this function tries to create a common extension for it.
     * @return An extension (without the dot), or an empty string if the mime-type is unknown.
     * @since MMBase-1.7.1
     */
    public String mimeTypeToExtension(String mimeType) {
        for (Detector detector : getDetectors()) {
            if (mimeType.equalsIgnoreCase(detector.getMimeType())) {
                Iterator<String> j = detector.getExtensions().iterator();
                if (j.hasNext()) {
                    String ex = j.next();
                    return ex;
                }
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
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
     * @javadoc
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
            for (Detector d : magicFile.getDetectors()) {
                log.info(d.toString());
            }
        }
    }
}
