/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.magicfile;

import java.io.*;
import java.util.List;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Tries to determine the mime-type of an input stream ({@link #getMimeType(InputStream)}) (or a file {@link
 * #getMimeType(File)}). It uses for that a bunch of {@link Detector}, which are all configured via a configuratin file
 * 'magic.xml', that you can find as a resource the jar as org/mmbase/config/magic.xml (you could (temporary) override
 * it if you need an improvement in that XML).
 *
 * Several algoritms are used to determin this mime-type. It can use 'magic' bytes sequences as also is done by the unix
 * 'file' utility. Most recognized file types are determined in that manner {@link BasicDetector}).
 *
 * XML based file types can also be determined by trying the parse the file as an XML and check for certain
 * XML-namespaces or public ids of the DTD (see {@link XmlDetector}).
 *
 * To recognize some other formats (noticebly microsoft office formats) there is now also recognition based on the
 * contents of zip-files. So it wil be tried to treat the stream as a zip-file and if that succeeds, it is checked
 * wether certain 'magic' files are present in it (see {@link ZipDetector}).
 *
 * @author cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class MagicFile {
    private static final Logger log = Logging.getLoggerInstance(MagicFile.class);

    protected static final int BUFSIZE = 4598;
    // Read a string of maximally this length from the file
    // Is this garanteed to be big enough?


    /**
     * A constant that is used as a return value for several methods in this class
     * to indidate that determining a mime type failed.
     */
    public static final String FAILED = "Failed to determine type";
    // application/octet-stream?

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
     * Determins what kind of file the given file represents.
     * It first considers the <em>contents</em> of the file using
     * {@link #getMimeType(InputStream)}. If that fails the extension of the file name is used
     * via {@link #extensionToMimeType} to guess the file's type.
     *
     * @param file Location of file to be checked
     * @return Type of the file as determined by the magic file
     */
    public String getMimeType(File file) throws IOException {
        FileInputStream fir = null;
        try {
            fir = new FileInputStream(file);
            String result =  getMimeType(fir);
            if (result == FAILED) {
                String fileName = file.getName();
                int i = fileName.lastIndexOf(".");
                if (i > 0) {
                    String  extension = fileName.substring(i + 1);
                    if (extension.length() > 0) {
                        result = extensionToMimeType(fileName.substring(i + 1));
                    }
                }
            }
            return result;
        } finally {
            if (fir != null) {
                fir.close();
            }
        }
    }

    /**
     * Tests the byte[] array for the mime type.
     *
     * @return The found mime-type or FAILED
     * @since MMBase-2.0
     */
    protected String getMimeType(final byte[] input,  final InputStream in) throws IOException {
        if (! in.markSupported()) {
            throw new IllegalArgumentException("Mark not supported on " + in);
        }
        List<Detector> list = getDetectors();
        if (list == null || list.size() == 0) {
            log.warn("No detectors found");
            return FAILED;
        }
        byte[] lithmus;
        if (input.length > BUFSIZE) {
            lithmus = new byte[BUFSIZE];
            System.arraycopy(input, 0, lithmus, 0, BUFSIZE);
            log.debug("getMimeType was called with big bytearray cutting to " + BUFSIZE + " bytes");
        } else {
            lithmus = input;
        }

        for (Detector detector : list) {
            in.reset();
            if (detector.test(lithmus, in)) {
                log.debug("Matched " + detector);
                return detector.getMimeType();
            }
        }
        return FAILED;
    }

    public String getMimeType(final byte[] input) {
        try {
            return getMimeType(input, new ByteArrayInputStream(input));
        } catch (IOException ioe) {
            log.error(ioe);
            return FAILED;
        }
    }
    /**
     * Determins what kind of file the given input stream represents.
     * @return The found mime-type or FAILED
     * @since MMBase-1.9.2
     */
    public String getMimeType(InputStream input) throws IOException {
        byte[] lithmus = new byte[BUFSIZE];
        if (! input.markSupported()) {
            input = new SerializableInputStream(input, -1);
            //throw new IllegalArgumentException("Mark not supported on " + in);
        }
        int res = input.read(lithmus, 0, BUFSIZE);
        if (log.isDebugEnabled()) {
            log.debug("read " + res + "  bytes from " + input);
        }
        return getMimeType(lithmus, input);
    }

    /**
     * Given a file's extension returns its mimetype (e.g. image/png).
     * @return A mimetype string or {@link #FAILED}.
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
     * @return An extension (without the dot), or an empty string if the mime-type is unknown, or '???'
     * if no valid extension for it is found.
     * @since MMBase-1.7.1
     */
    public String mimeTypeToExtension(String mimeType) {
        for (Detector detector : getDetectors()) {
            if (mimeType.equalsIgnoreCase(detector.getMimeType())) {
                for (String ex : detector.getExtensions()) {
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
