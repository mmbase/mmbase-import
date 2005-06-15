/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.xml.Mmxf;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.util.transformers.XmlField;
import java.util.*;
import java.util.regex.*;
import java.util.regex.Matcher;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.mmbase.util.logging.*;


/**
 * Set-processing for an `mmxf' field. This is the counterpart and inverse of {@link MmxfGetString}, for more
 * information see the javadoc of that class.
 * @author Michiel Meeuwissen
 * @version $Id: MmxfSetString.java,v 1.5 2005-06-15 15:50:18 michiel Exp $
 * @since MMBase-1.8
 */

public class MmxfSetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(MmxfSetString.class);

    private static XmlField xmlField = new XmlField(XmlField.WIKI);

    private static long indexCounter = System.currentTimeMillis() / 1000;
    private Document parse(String value)  throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException,  java.io.IOException {
        try {
            return parse(new java.io.ByteArrayInputStream(value.getBytes("UTF-8")));
        } catch (java.io.UnsupportedEncodingException uee) {
            // cannot happen, UTF-8 is supported..
            return null;
        }

    }
    private Document parse(java.io.InputStream value)  throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException,  java.io.IOException {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setValidating(false);
        dfactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
        // dont log errors, and try to process as much as possible...
        XMLErrorHandler errorHandler = new XMLErrorHandler(false, org.mmbase.util.XMLErrorHandler.NEVER);
        documentBuilder.setErrorHandler(errorHandler);

        documentBuilder.setEntityResolver(new XMLEntityResolver(false));       
        Document doc = documentBuilder.parse(value);
        if (! errorHandler.foundNothing()) {
            throw new IllegalArgumentException("xml invalid:\n" + errorHandler.getMessageBuffer() + "for xml:\n" + value);
        }
        return doc;
    }



    private final int MODE_SECTION = 0;
    private final int MODE_INLINE  = 1;
    private class ParseState {
        int level = 0;
        int offset = 0;
        int mode;
        List subSections;
        ParseState(int sl, int m) {
            this(sl, m, 0);
        }
        ParseState(int sl, int m, int of) {
            level = sl;
            mode = m;
            offset = of;
            if (m == MODE_SECTION)  subSections = new ArrayList();
        }

        public String level() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0 ; i < level ; i++) buf.append("  ");
            return buf.toString();

        }
    }


    private static Pattern copyElement   = Pattern.compile("table|tr|td|th|em|strong|ul|ol|li|p|sub|sup");
    private static Pattern ignoreElement = Pattern.compile("tbody|thead");
    private static Pattern ignore        = Pattern.compile("link|#comment");
    private static Pattern hElement      = Pattern.compile("h([1-9])");
    private static Pattern crossElement  = Pattern.compile("a|img");


    private void copyAttributes(Element source, Element destination) {
        NamedNodeMap attributes = source.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            org.w3c.dom.Node n = attributes.item(i);
            destination.setAttribute(n.getNodeName(), n.getNodeValue());
        }
    }


    private void parseKupu(Element source, Element destination, List links, ParseState state) {
        org.w3c.dom.NodeList nl = source.getChildNodes();
        log.debug(state.level() + state.level + " Appending to " + destination.getNodeName() + " at " + state.offset + " of " + nl.getLength());
        for (; state.offset < nl.getLength(); state.offset++) {
            org.w3c.dom.Node node = nl.item(state.offset);
            if (node == null) break;
            String name= node.getNodeName();
            Matcher matcher = ignore.matcher(name);
            if (matcher.matches()) {
                continue;            
            }
            if (name.equals("#text")) {
                if (node.getNodeValue() != null && ! "".equals(node.getNodeValue().trim())) {
                    if (state.mode == MODE_SECTION) {
                        Element imp = destination.getOwnerDocument().createElement("p");
                        log.debug("Appending to " + destination.getNodeName());
                        destination.appendChild(imp);
                        Text text = destination.getOwnerDocument().createTextNode(node.getNodeValue());
                        imp.appendChild(text);
                    } else {
                        Text text = destination.getOwnerDocument().createTextNode(node.getNodeValue());
                        destination.appendChild(text);
                    }
                } else {
                    log.debug("Ignored empty #text");
                }
                continue;
            }

            if (! (node instanceof Element)) {
                log.warn(" found node " + node.getNodeName() + " which is not an element!");
                continue;
            }

            matcher = ignoreElement.matcher(name);
            if (matcher.matches()) {
                parseKupu((Element) node, destination, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }

            matcher = crossElement.matcher(name);
            if (matcher.matches()) {
                Element imp = destination.getOwnerDocument().createElement("a");
                destination.appendChild(imp);
                copyAttributes((Element) node, imp);
                links.add(imp); 
                // could only do something for 'a', but well, never mind
                parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }
            if (name.equals("i")) { // should not happen
                Element imp = destination.getOwnerDocument().createElement("em");
                destination.appendChild(imp);
                parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }
            if (name.equals("b")) { // should not happen
                Element imp = destination.getOwnerDocument().createElement("strong");
                destination.appendChild(imp);
                parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }
            if (name.equals("br")  && state.mode == MODE_INLINE) { // sigh
                Element imp = destination.getOwnerDocument().createElement("br");
                destination.appendChild(imp);
                continue;
            }

            matcher = copyElement.matcher(name);
            if (matcher.matches()) {
                Element imp = destination.getOwnerDocument().createElement(matcher.group(0));
                destination.appendChild(imp);
                copyAttributes((Element) node, imp);
                parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }
            matcher = hElement.matcher(name);
            if (matcher.matches()) {
                if (state.mode != MODE_SECTION) {
                    log.warn("Found a sections where it cannot be! (h-tags need to be on root level");
                    // treat as paragraph
                    Element imp = destination.getOwnerDocument().createElement("p");
                    destination.appendChild(imp);
                    copyAttributes((Element) node, imp);
                    parseKupu((Element) node, imp, links,  new ParseState(state.level, MODE_INLINE));
                    continue;
                }

                int foundLevel = Integer.parseInt(matcher.group(1));

                log.debug(state.level() + " Found section " + foundLevel + " on " + state.level);
                if (foundLevel > state.level) {
                        // need to create a new state.
                    Element section = destination.getOwnerDocument().createElement("section"); 
                    Element h       = destination.getOwnerDocument().createElement("h");
                    section.appendChild(h);                        
                    if (foundLevel == state.level + 1) {
                        parseKupu((Element) node, h, links,  new ParseState(state.level, MODE_INLINE));
                        state.subSections.add(section);
                        ParseState newState = new ParseState(foundLevel, MODE_SECTION, state.offset + 1);
                        parseKupu(source, section, links, newState);
                        state.offset = newState.offset;
                    } else {
                        state.subSections.add(section);
                        ParseState newState = new ParseState(state.level + 1, MODE_SECTION, state.offset);
                        parseKupu(source, section, links, newState);
                        state.offset = newState.offset;
                    }
                    continue;

                } else {
                    // drop state;
                    log.debug("foundlevel " + foundLevel + " level " + state.level + " --> dropping");
                    while(! state.subSections.isEmpty()) {
                        log.debug("Appending to " + destination.getNodeName());
                        destination.appendChild((org.w3c.dom.Node) state.subSections.remove(0));
                    }
                    state.offset--;
                    return;
                }
            }
        }
        if (state.mode == MODE_SECTION) {
            // drop state;
            while(! state.subSections.isEmpty()) {
                destination.appendChild((org.w3c.dom.Node) state.subSections.remove(0));
            }                    
        }                
    }

    private NodeList get(Cloud cloud, NodeList list, String field, String value) {
        NodeList result = cloud.getCloudContext().createNodeList();
        NodeIterator i = list.nodeIterator();
        while(i.hasNext()) {
            Node n = i.nextNode();
            String pref = "" + list.getProperty(NodeList.NODESTEP_PROPERTY);
            String fieldName = field;
            if (fieldName.indexOf(".") == -1 && pref != null) {
                fieldName = pref + "." + field;
            }
            if (n.getStringValue(fieldName).equals(value)) {
                result.add(n);
            }
        }
        return result;
    }

    private Document parseKupu(Node editedNode, Document document) {
        if (log.isDebugEnabled()) {
            log.debug("Handeling kupu-input" + XMLWriter.write(document, false));
        }
        Document xml = Mmxf.createMmxfDocument();
        // first find Body.
        org.w3c.dom.NodeList bodies = document.getElementsByTagName("body");
        if (bodies.getLength() != 1) log.warn("Found more not one body but " + bodies.getLength());
        Element body = (Element) bodies.item(0);
        body.normalize();
        Element mmxf = xml.getDocumentElement();
        List links = new ArrayList();
        parseKupu(body, mmxf, links, new ParseState(0, MODE_SECTION));
        // now handle kupu-links.
        if (editedNode == null) {
            log.warn("Node node given, cannot handle cross-links!!");
        } else {
            Cloud cloud = editedNode.getCloud();
            NodeManager images = cloud.getNodeManager("images");
            NodeManager urls   = cloud.getNodeManager("urls");
            NodeList relatedImages        = Queries.getRelatedNodes(editedNode, images, "idrel", "destination", "id", null);
            NodeList relatedUrls          = Queries.getRelatedNodes(editedNode, urls ,  "idrel", "destination", "id", null);
            //log.info("Found related urls " + relatedUrls);
            Iterator linkIterator = links.iterator();
            //String imageServletPath = images.getFunctionValue("servletpath", null).toString();
            while (linkIterator.hasNext()) {
                Element a = (Element) linkIterator.next();
                String href = a.getAttribute("href");
                NodeList linkedUrls = get(cloud, relatedUrls, "url", href);
                String id;
                if (linkedUrls.isEmpty()) {
                    // no such related url found!
                    // create it!
                    Node newUrl = cloud.getNodeManager("urls").createNode();
                    newUrl.setStringValue("url", href);
                    newUrl.setStringValue("title", a.getAttribute("alt"));
                    newUrl.commit();
                    RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), newUrl.getNodeManager(), "idrel");
                    Relation newIdRel = rm.createRelation(editedNode, newUrl);
                    id = "_" + indexCounter++;
                    newIdRel.setStringValue("id", id);
                    newIdRel.commit();
                    
                } else {
                    // found!
                    // set id to correct value
                    id = linkedUrls.getNode(0).getStringValue("idrel.id");
                }
                a.setAttribute("id", id);
                a.removeAttribute("href");
            }
        }
        
        
        return xml;

    }

    
    public Object process(Node node, Field field, Object value) {
        

        try {
            switch(MmxfGetString.getMode(node.getCloud().getProperty(Cloud.PROP_XMLMODE))) {
            case MmxfGetString.MODE_KUPU: {
                log.debug("Handeling kupu-input: " + value);
                return parseKupu(node, parse("" + value));
            }
            case MmxfGetString.MODE_WIKI: {
                log.debug("Handling wiki-input: " + value);
                return  parse(xmlField.transformBack("" + value));
            }
            case MmxfGetString.MODE_FLAT: {
                return parse(xmlField.transformBack("" + value));
            }
            default: {
                // 'raw' xml
                try {
                    return parse("" + value);
                } catch (Exception e) {
                    log.warn("Setting field " + field + " in node " + node.getNumber() + ", but " + e.getMessage());
                    // simply Istore it, as provided, then.
                    // fall trough
                }
                return value;
            } 

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return value;
        }
    }

    /**
     * Invocation of the class from the commandline for testing. Uses RMMCI (on the default
     * configuration), gets the 'xmltest' node, and get and set processes it.
     */
    public static void main(String[] argv) {
        if (System.getProperty("mmbase.config") == null) {
            System.err.println("Please start up with -Dmmbase.config=<mmbase configuration directory> (needed to find the XSL's)");
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
                System.out.println(org.mmbase.util.xml.XMLWriter.write(document, false));
            } else {
                MmxfSetString setProcessor = new MmxfSetString();
                ResourceLoader rl = ResourceLoader.getSystemRoot();
                Document doc = setProcessor.parse(rl.getResourceAsStream(argv[0]));
                Node node = null;
                if (argv.length > 1) {
                    CloudContext cc = ContextProvider.getDefaultCloudContext();
                    Cloud cloud = cc.getCloud("mmbase", "class", null);
                    node = cloud.getNode(argv[1]);
                }
                Document mmxf = setProcessor.parseKupu(node, doc);
                if (node != null) {
                    log.info("Setting body of " + node.getNumber() + " to " + XMLWriter.write(mmxf, false));
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
  
}
