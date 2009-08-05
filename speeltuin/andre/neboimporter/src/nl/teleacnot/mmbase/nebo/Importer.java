package nl.teleacnot.mmbase.nebo;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.ResourceLoader;
import org.mmbase.util.XSLTransformer;
import org.mmbase.util.xml.UtilReader;

import java.io.*;
import java.util.Map;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;


/**
 * Transforms an xml with data to import using a xsl to a somewhat nicer formated xml that's
 * easier to import. When finished it calls the class that does the actual importing in MMBase.
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 *
 */
public final class Importer {
    
    private static final Logger log = Logging.getLoggerInstance(Importer.class);

    public static final String CONFIG_FILE = "nebo-importer.xml";
    
    /* the xsl stylesheet used for the transformation, should be placed in 'config/xslt' */
    public static String xsl = "nebo2mmbase.xsl";
    /* xml file to import with the crude NEBO data */
    public static String inputXML = "stream_Uitzendinggemistfeed.xml";
    /* the resulting xml that is read and imported in MMBase */
    public static String resultXML = "streams.xml";
    
    private static final UtilReader reader = new UtilReader(CONFIG_FILE, new Runnable() {
                                                 public void run() {
                                                     readConfiguration(reader.getProperties());
                                                 }
                                             });
    
    /**
     * Constructor: inits configuration
     */
    public Importer () {
        readConfiguration(reader.getProperties());
    }
    
    /**
     * Reads configuration
     *
     * @param configuration config properties 
     * 
     */
    synchronized static void readConfiguration(Map configuration) {
        String tmp = (String) configuration.get("xsl");
        if (tmp != null && !tmp.equals("")) {
            xsl = tmp;
            log.debug("Importer's xsl stylesheet: "    + xsl);
        }        
        tmp = (String) configuration.get("inputXML");
        if (tmp != null && !tmp.equals("")) {
            inputXML = tmp;
            log.debug("The xml to import: "    + inputXML);
        }
        tmp = (String) configuration.get("resultXML");
        if (tmp != null && !tmp.equals("")) {
            resultXML = tmp;
            log.debug("The resulting xml: "    + resultXML);
        }
    }    
    
    
    /**
     * General method that calls the others
     */
    public static void importXML() {
        importXML(xsl, inputXML, resultXML);
    }
    
    /**
     * General method that calls the others
     *
     * @param xsl       the stylesheet to use, which should be in the mmbase config dir xsl 
     * @param path      path to both the xml and resulting file, this directory should be writable for MMBase
     * @param xml       location of the xml to transform
     * @param filename  location and filename of the resulting xml file
     *
     */
    public static void importXML(String style, String xml, String result) {
        if ("".equals(xsl)) style = xsl;
        if ("".equals(xml)) xml = inputXML;
        if ("".equals(result)) result = resultXML;
        
        boolean transformed = XSLTransform(style, xml, result);
        log.info("Did it succeed: " + transformed);
        
        if (transformed) importXMLinMMBase(result);        
    }
    
    /**
     * Does the actual parsing of the transformed xml and imports the stuff in MMBase
     *
     * @param  xml  xml file to parse
     *
     */
    public static void importXMLinMMBase(String xml) {
        log.debug("xml: " + xml);
        try {    
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setCoalescing(true); // escaping van rare karakters
			dbf.setNamespaceAware(false);
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.parse(xml); // inlezen die hap ...
			
			NodeList streams = Streams.import2MMBase(doc);
			log.debug("Imported " + streams.size() + " streams, will send mail now.");
			
            Mail mail = new Mail(reader.getProperties());
            mail.sendMail(streams);
            
		} catch (IllegalArgumentException iae) {
		    log.error("Illegal argument while trying to import: "  + iae);
		} catch (ParserConfigurationException pce) {
		    log.error("Error configuring while trying to import: "  + pce);
		} catch (IOException ioe) {
		    log.error("IO error while trying to import: "  + ioe);
		} catch (org.xml.sax.SAXException se) {
		    log.error("XML error while trying to import: "  + se);
        }
    }
    
    /*
     * Transforms the NEBO xml to a more simple and usefull one which will be parsed by this class
     *
     * @param xsl       the stylesheet to use, which should be in the mmbase config dir xsl 
     * @param xml       location of the xml to transform
     * @param filename  location and filename of the resulting xml file
     *
     */
    private static boolean XSLTransform(String xsl, String xml, String filename) {
        log.debug("Transfroming with xsl: " + xsl + " and xml: " + xml + " to: " + filename);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException fne) {
            log.error("File '" + filename + "' could not be written: " + fne);
            return false;
        }
        try {
            java.net.URL xslURL = ResourceLoader.getConfigurationRoot().getResource("xslt/" + xsl);
            XSLTransformer.transform(new StreamSource(xml), xslURL, new StreamResult(fos), null);
            return true;
        } catch (TransformerException te) {
            log.error("XSL transforming failed: " + te);
            return false;
        }
    }

    public static void main (String[] args) {
        importXML();
    }
}
