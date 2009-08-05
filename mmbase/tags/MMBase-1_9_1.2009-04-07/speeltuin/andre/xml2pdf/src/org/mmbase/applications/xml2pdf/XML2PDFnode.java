package org.mmbase.applications.xml2pdf;

import java.io.*;
import java.util.Map;

// JAXP
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

// FOP
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

// MMBase
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.xml.UtilReader;

/**
 * This class converts an XML to a PDF and stores it as an attachment node.
 * It uses a XSL to convert the XML to an appropriate FO. This class should be implemented
 * as a MMBase function that can be called within jsp/taglib.
 * A lot of this code is based on the FopServlet example.
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
public class XML2PDFnode {
	
    private static Logger log = Logging.getLoggerInstance(XML2PDFnode.class);
	
	protected TransformerFactory transFactory = null;
    protected FopFactory fopFactory = null;

	protected static String xml;
	/* xsl's need to be in a directory 'xsl' */
	protected static String xsl;
	protected static String title = "Your generated pdf";
	protected static String description = "PDF generated with Apache FOP and MMBase";
	protected static String filename;
	protected static Cloud cloud;

    /* Config file for more application specific properties  */
    public static final String CONFIG_FILE = "xml2pdf.xml";	// lives in 'config/utils'
    private static final UtilReader reader = new UtilReader(CONFIG_FILE, new Runnable() {
                                                 public void run() {
                                                     readConfiguration(reader.getProperties());
                                                 }
                                             });

    /**
     * Constructor: inits configuration
     */
    public XML2PDFnode () {
        readConfiguration(reader.getProperties());

        this.transFactory = TransformerFactory.newInstance();
        //Configure FopFactory as desired
        this.fopFactory = FopFactory.newInstance();
        //configureFopFactory();
    }    
	
	/**
     * Reads configuration
     * @param configuration config properties 
     * 
     */
    synchronized static void readConfiguration(Map configuration) {
        String tmp = (String) configuration.get("title");
        if (tmp != null && !tmp.equals("")) {
            title = tmp;
            log.info("title: " + title);
        }        
        tmp = (String) configuration.get("xsl");
        if (tmp != null && !tmp.equals("")) {
            xsl = tmp;
            log.info("xsl stylesheet: " + xsl);
        }        
        tmp = (String) configuration.get("xml");
        if (tmp != null && !tmp.equals("")) {
            xml = tmp;
            log.info("xml to import: " + xml);
        }
    }

	/**
	 * The method that returns an attachments node with the PDF.
	 *
	 * @param  
	 * @return 
	 */
	public Node createPDFnode(Cloud cloud, String xml, String xsl, String title) throws FOPException, TransformerException, IOException {
		this.cloud = cloud; 
		if (xml != null && !"".equals(xml)) this.xml = xml;
		if (xsl != null && !"".equals(xsl)) this.xsl = xsl;
		if (title != null && !"".equals(title)) this.title = title;
		log.debug("title: " + title);
		log.debug("xml:   " + xml);
		log.debug("xslt: "  + xsl);
		
		java.net.URL xslURL = ResourceLoader.getConfigurationRoot().getResource("xslt/" + xsl);
		java.net.URL xmlURL = ResourceLoader.getWebRoot().getResource(xml);
		
		log.debug("xmlURL: " + xmlURL);
		log.debug("xsltURL: " + xslURL);
		
		Source xslsrc = new StreamSource(xslURL.toString());
		Source xmlsrc = new StreamSource(xmlURL.toString());
		
		Transformer tf = this.transFactory.newTransformer(xslsrc);
		
		return render(xmlsrc, tf);
	}
	

    /**
     * Renders an input file (XML or XSL-FO) into a PDF file. It uses the JAXP
     * transformer given to optionally transform the input document to XSL-FO.
     * The transformer may be an identity transformer in which case the input
     * must already be XSL-FO. The PDF is written to a byte array that is
     * returned as the method's result.
     * @param src Input XML or XSL-FO
     * @param transformer Transformer to use for optional transformation
     * @throws FOPException If an error occurs during the rendering of the XSL-FO
     * @throws TransformerException If an error occurs during XSL transformation
     * @throws IOException In case of an I/O problem
     */
    protected Node render(Source src, Transformer transformer) 
            throws FOPException, TransformerException, IOException {

        // Setup output
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Setup FOP
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		//foUserAgent.setBaseURL(ResourceLoader.getWebRoot().toString());
		foUserAgent.setProducer("Apache FOP via MMBase" + org.mmbase.Version.getNumber());
		foUserAgent.setTitle(title);
		
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

        // Make sure the XSL transformation's result is piped through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        // Start the transformation and rendering process
        transformer.transform(src, res);

        // Return the result
        return createNode(out.toByteArray());
    }

	/**
	 * Creates the attachment node and commits it to MMBase
	 *
	 * @param  pdfile
	 * @return an attachments node with the pdf
	 */
	public Node createNode(byte[] pdfile) {
		NodeManager nm = cloud.getNodeManager("attachments");
		Node n = nm.createNode();
        n.setStringValue("title", title);
		description = description + " (xsl stylesheet: " + xsl + ", xml used:" + xml + ")";
        n.setStringValue("description", description);
		if ("".equals(filename)) filename = title.toLowerCase();
        n.setStringValue("filename", filename);
        n.setByteValue("handle", pdfile);
        n.commit();
		
		return n;
	}


}
