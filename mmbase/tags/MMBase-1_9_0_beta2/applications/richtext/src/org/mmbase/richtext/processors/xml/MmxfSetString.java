/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext.processors.xml;

import org.mmbase.datatypes.processors.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.datatypes.processors.xml.Modes;
import org.mmbase.richtext.transformers.XmlField;

import org.w3c.dom.Document;
import org.mmbase.util.logging.*;


/**
 * Set-processing for an `mmxf' field. This is the counterpart and inverse of {@link MmxfGetString}, for more
 * information see the javadoc of that class.
 * @author Michiel Meeuwissen
 * @version $Id: MmxfSetString.java,v 1.24 2008-06-10 15:46:11 michiel Exp $
 * @since MMBase-1.8
 */

public class MmxfSetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(MmxfSetString.class);
    private static final long serialVersionUID = 1L;


    private static final XmlField xmlField    = new XmlField(XmlField.WIKI);
    private static final XmlField xmlFieldBrs = new XmlField(XmlField.WIKIBRS);
    private static final XmlField xmlFieldBr  = new XmlField(XmlField.WIKIBR);
    private static final Kupu     kupu     = new Kupu();
    private static final DocBook  docbook  = new DocBook();
    private static final Wiki     wiki     = new Wiki();




    // javadoc inherited
    public Object process(Node node, Field field, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Found for setstring " + value.getClass() + " " + Util.toString(value));
        }
        try {
            switch(Modes.getMode(node.getCloud().getProperty(Cloud.PROP_XMLMODE))) {
            case Modes.KUPU: {
                log.debug("Handeling kupu-input: " + Util.toString(value));
                return kupu.parse(node, Util.parse(value));
            }
            case Modes.WIKI: {
                if (log.isTraceEnabled()) {
                    log.trace("Handling wiki-input: " + value);
                }
                String xml;
                Object brsMode = node.getCloud().getProperty(MmxfGetString.MODE_BRS);
                if ("none".equals(brsMode)) {
                    xml = xmlField.transformBack(Util.toString(value).trim());
                } else if ("single".equals(brsMode)) {
                    xml = xmlFieldBr.transformBack(Util.toString(value).trim());
                } else {
                    xml = xmlFieldBrs.transformBack(Util.toString(value).trim());
                }
                if (log.isDebugEnabled()) {
                    log.debug("XML: " + xml);
                }
                return  wiki.parse(node, field, Util.parse(xml));
            }
            case Modes.DOCBOOK: {
                log.debug("Handling docbook-input: " + value);
                return  docbook.parse(node, Util.parse(value));
            }
            case Modes.FLAT: {
                log.debug("Handling flat-input " + value.getClass() + " " + Util.toString(value));
                return Util.parse(xmlField.transformBack(Util.toString(value)));
            }
            default: {
                // 'raw' xml
                try {
                    return Util.parse(value);
                } catch (Exception e) {
                    log.warn("Setting field " + field + " in node " + node.getNumber() + ", but " + e.getMessage());
                    // simply Istore it, as provided, then.
                    // fall trough
                }
                return value;
            }

            }
        } catch (Exception e) {
            log.error(e.getMessage() + " for " + value, e);
            return value;
        }
    }

    /**
     * Invocation of the class from the commandline for testing. Uses RMMCI (on the default
     * configuration), gets the 'xmltest' node, and get and set processes it.
     */
    public static void main(String[] argv) {
        if (System.getProperty("mmbase.config") == null) {
            System.err.println("Please start up with -Dmmbase.defaultcloudcontext=rmi://127.0.0.1:1111/remotecontext -Dmmbase.config=<mmbase configuration directory> (needed to find the XSL's)");
            return;
        }
        try {
            if (argv.length == 0) {
                CloudContext cc = ContextProvider.getDefaultCloudContext();
                Cloud cloud = cc.getCloud("mmbase", "class", null);

                Node node = cloud.getNode("xmltest");

                cloud.setProperty(Cloud.PROP_XMLMODE, "wiki");

                Processor getProcessor = new MmxfGetString();
                String wiki = (String) getProcessor.process(node, node.getNodeManager().getField("body"), null);

                System.out.println("in:\n" + wiki);

                Processor setProcessor = new MmxfSetString();

                System.out.println("\n-------------\nout:\n");
                Document document = (Document) setProcessor.process(node, node.getNodeManager().getField("body"), wiki);
                System.out.println(XMLWriter.write(document, false));
            } else {
                MmxfSetString setProcessor = new MmxfSetString();
                ResourceLoader rl = ResourceLoader.getSystemRoot();
                Document doc = Util.parse(rl.getResourceAsStream(argv[0]));
                Node node = null;
                if (argv.length > 1) {
                    CloudContext cc = ContextProvider.getDefaultCloudContext();
                    Cloud cloud = cc.getCloud("mmbase", "class", null);
                    if (argv.length > 2) {
                        cloud.setProperty(Cloud.PROP_XMLMODE, argv[2]);
                    }
                    node = cloud.getNode(argv[1]);
                }
                Document mmxf = kupu.parse(node, doc);
                if (node != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Setting body of " + node.getNumber() + " to " + XMLWriter.write(mmxf, false));
                    }
                    node.setXMLValue("body", mmxf);
                    node.commit();
                } else {
                    System.out.println(XMLWriter.write(mmxf, false));
                }

            }
        } catch (Exception e) {
            Throwable cause = e;
            while (cause != null) {
                System.err.println("CAUSE " + cause.getMessage() + Logging.stackTrace(cause));
                cause = cause.getCause();
            }
        }
        /*


        try{
            XMLSerializer serializer = new XMLSerializer();
            serializer.setNamespaces(true);
            serializer.setOutputByteStream(System.out);
            serializer.serialize(document);
        } catch (java.io.IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        */


    }

    public String toString() {
        return "set_MMXF";
    }

}
