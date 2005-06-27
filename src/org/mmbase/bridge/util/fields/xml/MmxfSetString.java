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
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.util.transformers.XmlField;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.mmbase.util.logging.*;


/**
 * Set-processing for an `mmxf' field. This is the counterpart and inverse of {@link MmxfGetString}, for more
 * information see the javadoc of that class.
 * @author Michiel Meeuwissen
 * @version $Id: MmxfSetString.java,v 1.9 2005-06-27 17:01:21 michiel Exp $
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



    /**
     * Means that we are on a level were <h> tags may follow, and subsections initiated
     */
    private final int MODE_SECTION = 0; 
    /**
     * Other levels, 
     */
    private final int MODE_INLINE  = 1;
    private class ParseState {
        int level = 0;
        int offset = 0;
        int mode;
        List subSections;
        List sparedTags;
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
                copyAttributes((Element) node, imp);
                if (state.mode == MODE_SECTION) {
                    Element p = destination.getOwnerDocument().createElement("p");
                    log.debug("Appending to " + destination.getNodeName());
                    destination.appendChild(p);
                    p.appendChild(imp);
                } else {
                    destination.appendChild(imp);
                }

                links.add(imp); 
                if ("generated".equals(imp.getAttribute("class"))) {
                    // body was generated by kupu, ignore that, it's only presentation.
                    log.info("Found generated body, ignoring that");
                } else {
                    // could only do something for 'a', but well, never mind
                    parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
                continue;
            }
            if (name.equals("i")) { // produced by FF
                Element imp = destination.getOwnerDocument().createElement("em");
                destination.appendChild(imp);
                parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }
            if (name.equals("b")) { // produced by FF
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
                    log.warn("Found a section where it cannot be! (h-tags need to be on root level");
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



    final Pattern ABSOLUTE_URL = Pattern.compile("(http[s]?://[^/]+)(.*)");
    protected String normalizeURL(HttpServletRequest request, String url) {

        if (url.startsWith("/")) {
            return url;
        }
        if (url.startsWith(".")) {
            if (request == null) {
                log.warn("Did not receive a request, don't know how to normalize '" + url + "'");
                return url;
            }
            // based on the request as viewed by the client.
            try {
                url = new URL(new URL(request.getRequestURL().toString()), url).toString();            
            } catch (java.net.MalformedURLException mfe) {
                log.warn("" + mfe); // should not happen
                return url;
            }
        }
        Matcher matcher = ABSOLUTE_URL.matcher(url);
        if (matcher.matches()) {
            if (request == null) {
                log.warn("Did not receive request, can't check if this URL is local: '" + url + "'");
                return url;
            }
            try {
                URL hostPart = new URL(matcher.group(1));
                if (hostPart.sameFile(new URL(request.getScheme(), request.getServerName(), request.getServerPort(), ""))) {
                    return matcher.group(2);
                } else {
                    return url;
                }
            } catch (java.net.MalformedURLException mfe) {
                log.warn("" + mfe); // client could have typed this.
                return url; // don't know anything better then this.
            }
        } else {
            log.warn("Could not normalize url " + url);
            return url;
        }

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
            NodeManager images      = cloud.getNodeManager("images");
            NodeManager icaches     = cloud.getNodeManager("icaches");
            NodeManager attachments = cloud.getNodeManager("attachments");
            NodeManager urls        = cloud.getNodeManager("urls");
            NodeManager segments    = cloud.getNodeManager("segments");

            String imageServlet      = images.getFunctionValue("servletpath", null).toString();
            String attachmentServlet = attachments.getFunctionValue("servletpath", null).toString();
            String segmentsServlet = "/mm18/mmbase/segments/";

            NodeList relatedImages        = Queries.getRelatedNodes(editedNode, images, "idrel", "destination", "id", null);
            NodeList usedImages           = cloud.getCloudContext().createNodeList();
            NodeList relatedUrls          = Queries.getRelatedNodes(editedNode, urls ,  "idrel", "destination", "id", null);
            NodeList usedUrls             = cloud.getCloudContext().createNodeList();
            NodeList relatedSegments      = Queries.getRelatedNodes(editedNode, segments , "idrel", "destination", "id", null);
            NodeList usedSegments         = cloud.getCloudContext().createNodeList();

            NodeList relatedAttachments   = Queries.getRelatedNodes(editedNode, attachments , "idrel", "destination", "id", null);
            NodeList usedAttachments      = cloud.getCloudContext().createNodeList();

            //log.info("Found related urls " + relatedUrls);

            //log.info("Found related urls " + relatedUrls);


            Iterator linkIterator = links.iterator();
            //String imageServletPath = images.getFunctionValue("servletpath", null).toString();
            while (linkIterator.hasNext()) {
                Element a = (Element) linkIterator.next();
                String href = a.getAttribute("href");
                if ("".equals(href)) {
                    href  = a.getAttribute("src");
                }
                // IE Tends to make URL's absolute (http://localhost:8070/mm18/mmbase/images/1234)
                // FF Tends to make URL's relative (../../../../mmbase/images/1234)
                // What we want is absolute on server (/mm18/mmbase/images/1234), because that is how URL was probably given in the first place.

                href = normalizeURL((HttpServletRequest) cloud.getProperty("request"), href);
                String klass = a.getAttribute("class");
                String id = a.getAttribute("id");
                if (id.equals("")) {
                    id = "_" + indexCounter++;
                    a.setAttribute("id", id);
                }

                if (href.startsWith(imageServlet)) { // found an image!
                    String q = "/images/" + href.substring(imageServlet.length());
                    BridgeServlet.QueryParts qp = BridgeServlet.readServletPath(q);
                    if (qp == null) {
                        log.error("Could not parse " + q + ", ignoring...");
                        continue;
                    }
                    String nodeNumber = qp.getNodeNumber();
                    Node image = cloud.getNode(nodeNumber);
                    if (image.getNodeManager().equals(icaches)) {
                        image = image.getNodeValue("id");
                    }
                    usedImages.add(image);
                    NodeList linkedImage = get(cloud, relatedImages, "idrel.id", id);
                    if (! linkedImage.isEmpty()) {
                        // ok, already related!
                        log.info("" + image + " image already correctly related, nothing needs to be done");
                        Node idrel = linkedImage.getNode(0).getNodeValue("idrel");
                        if (!idrel.getStringValue("class").equals(klass)) {
                            idrel.setStringValue("class", klass);
                            idrel.commit();
                        }
                        
                    } else {
                        log.service(" to" + image + ", creating new relation");
                        RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), images, "idrel");
                        Relation newIdRel = rm.createRelation(editedNode, image);
                        newIdRel.setStringValue("id", id);
                        newIdRel.setStringValue("class", klass);
                        newIdRel.commit();                        
                    }
                    a.removeAttribute("src");
                    a.removeAttribute("height");
                    a.removeAttribute("width");
                    a.removeAttribute("class");
                    a.removeAttribute("alt");
                } else if (href.startsWith(segmentsServlet)) {
                    String nodeNumber = href.substring(segmentsServlet.length());
                    if (! cloud.hasNode(nodeNumber)) {
                        log.error("No such node '" + nodeNumber + "' (deduced from " + href + ")");
                        continue;
                    }
                    Node segment = cloud.getNode(nodeNumber);
                    usedSegments.add(segment);
                    NodeList linkedSegment = get(cloud, relatedSegments, "idrel.id", id);
                    if (! linkedSegment.isEmpty()) {
                        // ok, already related!
                        log.info("" + segment + " image already correctly related, nothing needs to be done");
                        Node idrel = linkedSegment.getNode(0).getNodeValue("idrel");
                        if (!idrel.getStringValue("class").equals(klass)) {
                            idrel.setStringValue("class", klass);
                            idrel.commit();
                        }
                        
                    } else {
                        log.info("Found new cross link " + segment.getNumber() + ", creating new relation now");
                        RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), segments, "idrel");
                        Relation newIdRel = rm.createRelation(editedNode, segment);
                        newIdRel.setStringValue("id", id);
                        newIdRel.setStringValue("class", klass);
                        newIdRel.commit();                        
                    }
                    a.removeAttribute("href");
                    a.removeAttribute("alt");
                } else if (href.startsWith(attachmentServlet)) { // an attachment
                    String q = "/attachments/" + href.substring(attachmentServlet.length());
                    BridgeServlet.QueryParts qp = BridgeServlet.readServletPath(q);
                    if (qp == null) {
                        log.error("Could not parse " + q + ", ignoring...");
                        continue;
                    }
                    String nodeNumber = qp.getNodeNumber();
                    Node attachment = cloud.getNode(nodeNumber);
                    usedAttachments.add(attachment);
                    NodeList linkedAttachment = get(cloud, relatedAttachments, "idrel.id", id);
                    if (! linkedAttachment.isEmpty()) {
                        // ok, already related!
                        log.service("" + attachment + " image already correctly related, nothing needs to be done");
                        Node idrel = linkedAttachment.getNode(0).getNodeValue("idrel");
                        if (!idrel.getStringValue("class").equals(klass)) {
                            idrel.setStringValue("class", klass);
                            idrel.commit();
                        }
                        
                    } else {
                        log.service(" to " + attachment + ", creating new relation");
                        RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), attachments, "idrel");
                        Relation newIdRel = rm.createRelation(editedNode, attachment);
                        newIdRel.setStringValue("id", id);
                        newIdRel.setStringValue("class", klass);
                        newIdRel.commit();                        
                    }
                    a.removeAttribute("href");
                    a.removeAttribute("class");
                    a.removeAttribute("title");
                    a.removeAttribute("target");
                } else { // must have been really an URL
                
                    NodeList idLinkedUrls = get(cloud, relatedUrls, "idrel.id", id);
                    if (!idLinkedUrls.isEmpty()) {
                        Node url   = idLinkedUrls.getNode(0).getNodeValue("urls");
                        Node idrel = idLinkedUrls.getNode(0).getNodeValue("idrel");
                        log.info("" + url + " url already correctly related, nothing needs to be done");
                        usedUrls.add(url);
                        if (!idrel.getStringValue("class").equals(klass)) {
                            idrel.setStringValue("class", klass);
                            idrel.commit();
                        }
                    } else {
                        NodeList nodeLinkedUrls = get(cloud, relatedUrls, "url", href); // perhaps
                                                                                        // search in
                                                                                        // entire cloud?
                        Node url;
                        if (nodeLinkedUrls.isEmpty()) {
                            // no such related url found!
                            // create it!
                            url = cloud.getNodeManager("urls").createNode();
                            url.setStringValue("url", href);
                            url.setStringValue("title", a.getAttribute("alt"));
                            url.commit();
                        } else {
                            url = nodeLinkedUrls.getNode(0).getNodeValue("urls");
                        }
                        usedUrls.add(url);
                        RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), url.getNodeManager(), "idrel");
                        Relation newIdRel = rm.createRelation(editedNode, url);
                        
                        newIdRel.setStringValue("id", id);
                        newIdRel.setStringValue("class", klass);
                        newIdRel.commit();                        
                    }
                    a.removeAttribute("href");
                    a.removeAttribute("title");
                    a.removeAttribute("target");
                    a.removeAttribute("class");
                }


            }
            // ready handling links. Now clean up unused links.
            log.service("Cleaning dangling idrels");
            cleanDanglingIdRels(relatedImages,   usedImages,   "images");
            cleanDanglingIdRels(relatedUrls,     usedUrls,     "urls");
            cleanDanglingIdRels(relatedSegments, usedSegments, "segments1");
            cleanDanglingIdRels(relatedAttachments, usedAttachments, "attachments");
        }
        
        
        return xml;

    }
    public void cleanDanglingIdRels(NodeList clusterNodes, NodeList usedNodes, String type) {
       NodeIterator i = clusterNodes.nodeIterator();            
       while(i.hasNext()) {
           Node clusterNode = i.nextNode();
           Node node = clusterNode.getNodeValue(type);
           log.service("Considering " + clusterNode);
           if (! usedNodes.contains(node)) {
               Node idrel = clusterNode.getNodeValue("idrel");
               if (idrel.mayDelete()) {
                   log.service("Removing unused irel " + idrel);
                   idrel.delete(true);
               }
           }
       }
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
