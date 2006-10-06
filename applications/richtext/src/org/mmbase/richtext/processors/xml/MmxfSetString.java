/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext.processors.xml;
import org.mmbase.datatypes.processors.*;
import org.mmbase.datatypes.processors.xml.Modes;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.richtext.Mmxf;
import org.mmbase.bridge.util.Queries;
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.richtext.transformers.XmlField;
import java.util.*;
import java.util.regex.*;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import org.mmbase.util.logging.*;


/**
 * Set-processing for an `mmxf' field. This is the counterpart and inverse of {@link MmxfGetString}, for more
 * information see the javadoc of that class.
 * @author Michiel Meeuwissen
 * @version $Id: MmxfSetString.java,v 1.13 2006-10-06 14:25:11 michiel Exp $
 * @since MMBase-1.8
 */

public class MmxfSetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(MmxfSetString.class);
    private static final long serialVersionUID = 1L;


    private static XmlField xmlField = new XmlField(XmlField.WIKI);

    /**
     * Used for generating unique id's
     */
    private static long indexCounter = System.currentTimeMillis() / 1000;

    /**
     * Just parses String to Document
     */
    protected static Document parse(Object value)  throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException,  java.io.IOException {
        if (value instanceof Document) return (Document) value;
        try {
            return parse(new java.io.ByteArrayInputStream(("" + value).getBytes("UTF-8")));
        } catch (java.io.UnsupportedEncodingException uee) {
            // cannot happen, UTF-8 is supported..
            return null;
        }

    }
    /**
     * Just parses InputStream  to Document (without validation).
     */
    protected static  Document parse(java.io.InputStream value)  throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException,  java.io.IOException {
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

    /**
     * Also used for parsing kupu-output
     */
    private class ParseState {
        int level = 0;
        int offset = 0;
        int mode;
        List<Element> subSections;
        ParseState(int sl, int m) {
            this(sl, m, 0);
        }
        ParseState(int sl, int m, int of) {
            level = sl;
            mode = m;
            offset = of;
            if (m == MODE_SECTION)  subSections = new ArrayList<Element>();
        }

        public String level() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0 ; i < level ; i++) buf.append("  ");
            return buf.toString();

        }
    }


    /**
     * Patterns used in parsing of kupu-output
     */

    private static Pattern copyElement   = Pattern.compile("table|tr|td|th|em|strong|ul|ol|li|p|sub|sup");
    private static Pattern ignoreElement = Pattern.compile("tbody|thead|font|span|acronym|address|abbr|base|blockquote|cite|code|pre|colgroup|col|dd|dfn|dl|dt|kbd|meta|samp|script|style|var|center");
    private static Pattern ignore        = Pattern.compile("link|#comment");
    private static Pattern hElement      = Pattern.compile("h([1-9])");
    private static Pattern crossElement  = Pattern.compile("a|img|div");
    private static Pattern divClasses    = Pattern.compile(".*\\bfloat (?:note left|note right|intermezzo|caption left|caption right|quote left|quote right)");

    private static Pattern allowedAttributes = Pattern.compile("id|href|src|class|type");

    private void copyAttributes(Element source, Element destination) {
        NamedNodeMap attributes = source.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            org.w3c.dom.Node n = attributes.item(i);
            if (allowedAttributes.matcher(n.getNodeName()).matches()) {
                destination.setAttribute(n.getNodeName(), n.getNodeValue());
            }
        }
    }

    private void copyChilds(Element source, Element destination) {
        org.w3c.dom.Node child = source.getFirstChild();
        while (child != null) {
            org.w3c.dom.Node copy = destination.getOwnerDocument().importNode(child, true);
            destination.appendChild(copy);
            child = child.getNextSibling();
        }
    }

    /**
     * @since MMBase-1.9
     */
    protected String getCssClass(String cl) {
        List<String> classes = new ArrayList();
        for (String c : cl.split("\\s+")) {
            if (! classes.contains(c)) classes.add(c);
        }
        StringBuffer c = new StringBuffer();
        Iterator<String> i = classes.iterator();
        while (i.hasNext()) {
            c.append(i.next());
            if (i.hasNext()) {
                c.append(" ");
            }
        }
        return c.toString();
    }

    /**
     * First stage of parsing kupu-output. Does nothing with relations, only cleans up to 'mmxf' XML.
     *
     * @param source       XML as received from kupu
     * @param destination  pseudo MMXF which is going to receive it.
     * @param links        This list collects elements representing some kind of link (cross-links, images, attachments, urls). Afterwards these can be compared with actual MMBase objects.
     * @param state        The function is called recursively, and this object remembers the state then (where it was while parsing e.g.).
     */

    private void parseKupu(Element source, Element destination, List<Element> links, ParseState state) {
        org.w3c.dom.NodeList nl = source.getChildNodes();
        if (log.isDebugEnabled()) {
            log.trace(state.level() + state.level + " Appending to " + destination.getNodeName() + " at " + state.offset + " of " + nl.getLength());
        }
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
                if (name.equals("div")) {
                    String cssClass = getCssClass("div " + imp.getAttribute("class"));
                    if (! divClasses.matcher(cssClass).matches()) { 
                        // this is no div of ours (copy/pasting?), ignore it.
                        parseKupu((Element) node, destination, links, new ParseState(state.level, MODE_INLINE));
                        continue;
                    } else {
                        imp.setAttribute("class", getCssClass("div " + imp.getAttribute("class")));
                    }
                }
                if (state.mode == MODE_SECTION) {
                    Element p = destination.getOwnerDocument().createElement("p");
                    log.debug("Appending to " + destination.getNodeName());
                    destination.appendChild(p);
                    p.appendChild(imp);
                } else {
                    destination.appendChild(imp);
                }

                links.add(imp);
                if (name.equals("div")) {
                    // don't treat body, will be done later, when handling 'links'.
                    // so simply copy everthing for now
                    copyChilds((Element) node, imp);

                } else {
                    if ("generated".equals(imp.getAttribute("class"))) {
                        // body was generated by kupu, ignore that, it's only presentation.
                        log.debug("Found generated body, ignoring that");
                    } else {
                        // could only do something for 'a' and 'div', but well, never mind
                        parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                    }
                }
                continue;
            }
            if (name.equals("i")) { // produced by FF
                if (node.getFirstChild() != null) { // ignore if empty
                    Element imp = destination.getOwnerDocument().createElement("em");
                    destination.appendChild(imp);
                    parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
                continue;
            }
            if (name.equals("b")) { // produced by FF
                if (node.getFirstChild() != null) { // ignore if empty
                    Element imp = destination.getOwnerDocument().createElement("strong");
                    destination.appendChild(imp);
                    parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
                continue;
            }
            if (name.equals("br")  && state.mode == MODE_INLINE) { // sigh
                Element imp = destination.getOwnerDocument().createElement("br");
                destination.appendChild(imp);
                continue;
            }

            matcher = copyElement.matcher(name);
            if (matcher.matches()) {
                org.w3c.dom.Node firstChild = node.getFirstChild();
                if (firstChild != null && !(firstChild.getNodeType() == org.w3c.dom.Node.TEXT_NODE && firstChild.getNodeValue().equals(""))) { // ignore if empty
                    Element imp = destination.getOwnerDocument().createElement(matcher.group(0));
                    destination.appendChild(imp);
                    copyAttributes((Element) node, imp);
                    parseKupu((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
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
            log.warn("Unrecognised element " + name + " ignoring");
            parseKupu((Element) node, destination, links, new ParseState(state.level, MODE_INLINE));
        }
        if (state.mode == MODE_SECTION) {
            // drop state;
            while(! state.subSections.isEmpty()) {
                destination.appendChild((org.w3c.dom.Node) state.subSections.remove(0));
            }
        }
    }

    /**
     * Just searches the nodes in a NodeList for which a certain field has a certain value.
     */
    private NodeList get(Cloud cloud, NodeList list, String field, String value) {
        NodeList result = cloud.createNodeList();
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

    /**
     * Normalizes URL to absolute on server
     */
    protected String normalizeURL(final HttpServletRequest request, final String url) {

        if (url.startsWith("/")) {
            return url;
        }
        String u = url;
        if (url.startsWith(".")) {
            if (request == null) {
                log.warn("Did not receive a request, don't know how to normalize '" + url + "'");
                return url;
            }


            try {
                // based on the request as viewed by the client.
                if (log.isDebugEnabled()) {
                    log.debug("Request of " + request.getAttribute("time") + " " + Collections.list(request.getAttributeNames()));
                }
                String requestURL = (String) request.getAttribute("javax.servlet.include.servlet_path");
                if (request.getScheme() == null) {
                    log.warn("How odd, we got a request with no scheme!!");
                }
                if (requestURL == null) {
                    requestURL = request.getRequestURL().toString();
                }
                u = new URL(new URL(requestURL), url).toString();
            } catch (java.net.MalformedURLException mfe) {
                log.warn("" + mfe, mfe); // should not happen
                return url;
            } catch (NullPointerException npe) {
                log.warn("NPE ", npe);
            }
        } else {
            u = url;
        }
        if (log.isDebugEnabled()) {
            log.debug("url " + url + " ->" + u);
        }
        Matcher matcher = ABSOLUTE_URL.matcher(u);
        if (matcher.matches()) {
            if (request == null) {
                log.warn("Did not receive request, can't check if this URL is local: '" + url + "'");
                return url;
            }
            try {
                URL hostPart = new URL(matcher.group(1));
                String scheme = request.getScheme();
                if (scheme == null) {
                    log.warn("Request " + request + " " + request.getRequestURI() + " gave 'null'  scheme" + request.getServerName() + ":" + request.getServerPort() + " " + request.getContextPath());
                }
                String host   = request.getServerName();
                int port      = request.getServerPort();
                URL foundHost = scheme != null ? new URL(scheme, host, port, "") : null;
                if (scheme != null && hostPart.sameFile(foundHost)) {
                    String result = matcher.group(2);
                    if (log.isDebugEnabled()) {
                        log.trace("Converted " + url + " -> " + result);
                    }
                    return result;
                } else {
                    if (log.isDebugEnabled()) {
                        log.trace("Not converting url, it is on a different server " + hostPart + " != " + foundHost);
                    }
                    return url;
                }
            } catch (java.net.MalformedURLException mfe) {
                log.warn("" + mfe); // client could have typed this.
                return url; // don't know anything better then this.
            }
        } else {
            log.debug("Could not normalize url " + url);
            return url;
        }

    }

    final Pattern OK_URL = Pattern.compile("[a-z]+:.*");

    /**
     * Adds missing protocol
     */
    protected String normalizeURL(String url) {
        if (OK_URL.matcher(url).matches() || (url.length() > 0 && url.charAt(0) == '/')) {
            return url;
        } else {
            return "http://" + url;
        }
    }

    protected Node getUrlNode(Cloud cloud, String href, Element a) {
        NodeManager urls = cloud.getNodeManager("urls");
        NodeQuery q = urls.createQuery();
        StepField urlStepField = q.getStepField(urls.getField("url"));
        Constraint c = q.createConstraint(urlStepField, href);
        q.setConstraint(c);
        NodeList ul = urls.getList(q);
        Node url;
        if (ul.size() > 0) {
            url = ul.getNode(0);
            log.service("linking to exsting URL from cloud " + url);
        } else {
            // not found, create it!
            url = cloud.getNodeManager("urls").createNode();
            url.setStringValue("url", href);
            if (urls.hasField("title")) {
                url.setStringValue("title", a.getAttribute("alt"));
            } else if (urls.hasField("name")) {
                url.setStringValue("name", a.getAttribute("alt"));
            }
            url.commit();
        }
        return url;
    }

    final Pattern DIV_ID = Pattern.compile("block_(.*?)_(.*)");


    // list related withouth inheritance
    private NodeList getRelatedNodes(Node editedNode, NodeManager dest) {
        NodeQuery q = Queries.createRelatedNodesQuery(editedNode, dest, "idrel", "destination");
        StepField stepField = q.createStepField(q.getNodeStep(), "otype");
        Constraint newConstraint = q.createConstraint(stepField, new Integer(dest.getNumber()));
        Queries.addConstraint(q, newConstraint);
        Queries.addRelationFields(q, "idrel", "id", null);
        return q.getCloud().getList(q);
    }


    private String getHref(Element a, Cloud cloud) {
        String href = a.getAttribute("href");
        if ("".equals(href)) {
            // must be an image then.
            // Images are _always_ on the same server.
            String src = a.getAttribute("src");
            try {
                href  = (new java.net.URI(src)).getPath();
            } catch (java.net.URISyntaxException se) {
                log.warn(se);
                href = src;
            }

        }
        String hrefBefore = href;
        if (! "".equals(href)) {
            if (! href.startsWith("mmbase:")) {
                href = normalizeURL((HttpServletRequest) cloud.getProperty("request"), href);
            }
        }

        // IE Tends to make URL's absolute (http://localhost:8070/mm18/mmbase/images/1234)
        // FF Tends to make URL's relative (../../../../mmbase/images/1234)
        // What we want is absolute on server (/mm18/mmbase/images/1234), because that is how URL was probably given in the first place.

        String klass = a.getAttribute("class");
        String id = a.getAttribute("id");

        if (klass.startsWith("div ") && href.equals("")) {
            klass = klass.substring(4);
            Matcher divId = DIV_ID.matcher(id);
            if (divId.matches()) {
                href = "BLOCK/" + divId.group(1);
                if (divId.group(1).equals("createddiv")) {
                    id = ""; // generate one
                }  else {
                    id   = divId.group(2);
                }
            } else {
                // odd
                href = "BLOCK/createddiv";
                id   = ""; // generated one
            }
            a.setAttribute("id", id);

        }
        if (id.equals("")) {
            id = "_" + indexCounter++;
            a.setAttribute("id", id);
        }
        if (log.isDebugEnabled()) {
            log.debug("Considering " + href + " (from " + hrefBefore + ")");
        }
        return href;
    }


    private boolean handleImage(String href, Element a, List<Map.Entry<String, Node>> usedImages, 
                                NodeList relatedImages, Node editedNode) {
        Cloud cloud = editedNode.getCloud();
        NodeManager images = cloud.getNodeManager("images");
        String  imageServlet      = images.getFunctionValue("servletpath", null).toString();
        if (! href.startsWith(imageServlet)) return false;
        String q = "/images/" + href.substring(imageServlet.length());
        log.debug(href + ":This is an image!!-> " + q);
        BridgeServlet.QueryParts qp = BridgeServlet.readServletPath(q);
        if (qp == null) {
            log.error("Could not parse " + q + ", ignoring...");
            return true;
        }
        NodeManager icaches     = cloud.getNodeManager("icaches");
        String nodeNumber = qp.getNodeNumber();
        Node image = cloud.getNode(nodeNumber);
        if (image.getNodeManager().equals(icaches)) {
            image = image.getNodeValue("id");
            log.debug("This is an icache for " + image.getNumber());
        }
        String klass = a.getAttribute("class");
        String id = a.getAttribute("id");
        usedImages.add(new Entry(id, image));
        NodeList linkedImage = get(cloud, relatedImages, "idrel.id", a.getAttribute("id"));
        if (! linkedImage.isEmpty()) {
            // ok, already related!
            log.service("" + image + " image already correctly related, nothing needs to be done");
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
        return true;
    }

    private boolean handleAttachment(Matcher matcher, Element a, List<Map.Entry<String, Node>>  usedAttachments, NodeList relatedAttachments, Node editedNode) {
        if (! matcher.matches()) return false;
        if (! matcher.group(1).equals("attachments")) return false;
        String nodeNumber = matcher.group(2);
        Cloud cloud = editedNode.getCloud();
        if (! cloud.hasNode(nodeNumber)) {
            log.error("No such node '" + nodeNumber + "' (deduced from " + matcher.group() + ")");
            return false;
        }
        NodeManager attachments = cloud.getNodeManager("attachments");
        Node attachment = cloud.getNode(nodeNumber);
        String klass = a.getAttribute("class");
        String id = a.getAttribute("id");
        usedAttachments.add(new Entry(id, attachment));
        NodeList linkedAttachment = get(cloud, relatedAttachments, "idrel.id", id);
        if (! linkedAttachment.isEmpty()) {
            // ok, already related!
            log.service("" + attachment + " attachment (class='" + klass + "') already correctly related, nothing needs to be done");
            Node idrel = linkedAttachment.getNode(0).getNodeValue("idrel");
            if (!idrel.getStringValue("class").equals(klass)) {
                idrel.setStringValue("class", klass);
                idrel.commit();
            }

        } else {
            log.service(" to " + attachment + "(class='" + klass+ "'), creating new relation");
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
        return true;
    }


    private boolean handleText(Matcher matcher, Element a, List<Map.Entry<String, Node>> usedTexts, NodeList relatedTexts, Node editedNode) {
        if (! matcher.matches()) return false;
        String nodeNumber = matcher.group(2);
        Cloud cloud = editedNode.getCloud();
        if (! cloud.hasNode(nodeNumber)) {
            log.error("No such node '" + nodeNumber + "' (deduced from " + matcher.group() + ")");
            return false;
        }
        Node text = cloud.getNode(nodeNumber);
        String klass = a.getAttribute("class");
        String id = a.getAttribute("id");
        usedTexts.add(new Entry(id, text));
        NodeList linkedText = get(cloud, relatedTexts, "idrel.id", id);
        if (! linkedText.isEmpty()) {
            // ok, already related!
            log.debug("" + text + " text already correctly related, nothing needs to be done");
            Node idrel = linkedText.getNode(0).getNodeValue("idrel");
            if (!idrel.getStringValue("class").equals(klass)) {
                idrel.setStringValue("class", klass);
                idrel.commit();
            }

        } else {
            log.service("Found new cross link " + text.getNumber() + ", creating new relation now");
            RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), text.getNodeManager(), "idrel");
            Relation newIdRel = rm.createRelation(editedNode, text);
            newIdRel.setStringValue("id", id);
            newIdRel.setStringValue("class", klass);
            newIdRel.commit();
        }

        a.removeAttribute("href");
        a.removeAttribute("alt");
        return true;
    }
    private boolean handleBlock(String href, Element a, List<Map.Entry<String, Node>> usedBlocks, NodeList relatedBlocks, Node editedNode) {
        if (! href.startsWith("BLOCK/")) return false;

        String nodeNumber = href.substring(6);
        Cloud cloud = editedNode.getCloud();
        NodeManager blocks = cloud.getNodeManager("blocks");
        final Node block;
        if (nodeNumber.equals("createddiv")) {
            block = blocks.createNode();
            block.setStringValue("title", "Block created for node " + editedNode.getNumber());
            block.commit();
        } else {
            block = cloud.getNode(nodeNumber);
        }
        DocumentBuilder documentBuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder();
        DOMImplementation impl = documentBuilder.getDOMImplementation();
        Document blockDocument = impl.createDocument("http://www.w3.org/1999/xhtml", "body", null);
        Element blockBody = blockDocument.getDocumentElement();
        copyChilds(a, blockBody);


        org.w3c.dom.Node child = a.getFirstChild();
        while (child != null) {
            a.removeChild(child);
            child = a.getFirstChild();
        }

        if (log.isDebugEnabled()) {
            log.debug("Setting body to " + XMLWriter.write(blockDocument, false));
        }
        // fill _its_ body, still in kupu-mode
        block.setStringValue("body", XMLWriter.write(blockDocument, false));
        block.commit();
        String klass = a.getAttribute("class");
        String id = a.getAttribute("id");
        usedBlocks.add(new Entry(id, block));
        NodeList linkedBlock = get(cloud, relatedBlocks, "idrel.id", id);
        if (! linkedBlock.isEmpty()) {
            // ok, already related!
            log.service("" + block + " block already correctly related, nothing needs to be done");
            Node idrel = linkedBlock.getNode(0).getNodeValue("idrel");
            if (!idrel.getStringValue("class").equals(klass)) {
                idrel.setStringValue("class", klass);
                idrel.commit();
            }

        } else {
            log.service(" to " + block + ", creating new relation");
            RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), blocks, "idrel");
            Relation newIdRel = rm.createRelation(editedNode, block);
            newIdRel.setStringValue("id", id);
            newIdRel.setStringValue("class", klass);
            newIdRel.commit();
        }
        a.removeAttribute("class");
        return true;
    }
    /**
     * Parses kupu-output for a certain node. First it will translate the XHTML like kupu-output to
     * something very similar to MMXF, while collecting the 'links'. Then in a second stage these
     * links are compared with related nodes. So the side-effect may be removed, updated, and new
     * related nodes.
     *
     * @param editedNode MMBase node containing the MMXF field.
     * @param document   XML received from Kupu
     * @return An MMXF document.
     */
    private Document parseKupu(Node editedNode, Document document) {
        if (log.isDebugEnabled()) {
            log.debug("Handeling kupu-input" + XMLWriter.write(document, false));
        }
        Document xml = Mmxf.createMmxfDocument();
        // first find Body.
        org.w3c.dom.NodeList bodies = document.getElementsByTagName("body");
        if (bodies.getLength() > 1) {
            log.warn("Found not one body but " + bodies.getLength());
        } else if (bodies.getLength() == 0) {
            log.warn("No body found ");
            return xml;
        }
        Element body = (Element) bodies.item(0);
        body.normalize();
        Element mmxf = xml.getDocumentElement();
        List<Element> links = new ArrayList();

        // first stage.
        parseKupu(body, mmxf, links, new ParseState(0, MODE_SECTION));


        // second stage, handle kupu-links.
        if (editedNode == null) {
            log.warn("Node node given, cannot handle cross-links!!");
        } else {
            Cloud cloud = editedNode.getCloud();
            NodeManager images      = cloud.getNodeManager("images");
            NodeManager attachments = cloud.getNodeManager("attachments");
            NodeManager urls        = cloud.getNodeManager("urls");
            NodeManager blocks      = cloud.getNodeManager("blocks");

            NodeManager texts       = cloud.getNodeManager("object");
            Pattern mmbaseUrl         = Pattern.compile("mmbase://(.*?)/(\\d+)");


            NodeList relatedImages        = getRelatedNodes(editedNode, images);
            List<Map.Entry<String, Node>> usedImages = new ArrayList();

            NodeList relatedAttachments   = getRelatedNodes(editedNode, attachments);
            List<Map.Entry<String, Node>> usedAttachments = new ArrayList();

            NodeList relatedBlocks        = getRelatedNodes(editedNode, blocks);
            List<Map.Entry<String, Node>> usedBlocks = new ArrayList();

            NodeList relatedUrls          = getRelatedNodes(editedNode, urls);
            List<Map.Entry<String, Node>> usedUrls = new ArrayList();

            NodeList relatedTexts;
            List<Map.Entry<String, Node>> usedTexts;
            {
                NodeQuery q = Queries.createRelatedNodesQuery(editedNode, texts, "idrel", "destination");
                StepField stepField = q.createStepField(q.getNodeStep(), "otype");
                SortedSet<Integer> nonTexts = new TreeSet();
                nonTexts.add(images.getNumber());
                nonTexts.add(attachments.getNumber());
                nonTexts.add(blocks.getNumber());
                nonTexts.add(urls.getNumber());
                FieldValueInConstraint newConstraint = q.createConstraint(stepField, nonTexts);
                q.setInverse(newConstraint, true);
                Queries.addConstraint(q, newConstraint);
                Queries.addRelationFields(q, "idrel", "id", null);
                relatedTexts = q.getCloud().getList(q);
                if (log.isDebugEnabled()) {
                    log.debug("Found related texts " + relatedTexts);
                }
                usedTexts = new ArrayList();
            }


            for (Element a : links) {
            //String imageServletPath = images.getFunctionValue("servletpath", null).toString();
                try {
                    String href = getHref(a, cloud);
                    Matcher mmbaseMatcher =  mmbaseUrl.matcher(href);
                    if (handleImage(href, a, usedImages, relatedImages, editedNode)) { // found an image!
                        continue;
                    } else if (handleAttachment(mmbaseMatcher, a, usedAttachments, relatedAttachments, editedNode)) {
                        continue;
                    } else if (handleText(mmbaseMatcher, a, usedTexts, relatedTexts, editedNode)) {
                        continue;
                    } else if (handleBlock(href, a, usedBlocks, relatedBlocks, editedNode)) {
                        continue;
                    } else { // must have been really an URL
                        String klass = a.getAttribute("class");
                        String id = a.getAttribute("id");

                        NodeList idLinkedUrls = get(cloud, relatedUrls, "idrel.id", id);
                        if (!idLinkedUrls.isEmpty()) {
                            // already related.
                            Node url   = idLinkedUrls.getNode(0).getNodeValue("urls");
                            Node idrel = idLinkedUrls.getNode(0).getNodeValue("idrel");
                            if (url.getStringValue("url").equals(href)) {
                                log.service("" + url + " url already correctly related, nothing needs to be done");
                                usedUrls.add(new Entry(id, url));
                                if (!idrel.getStringValue("class").equals(klass)) {
                                    idrel.setStringValue("class", klass);
                                    idrel.commit();
                                }
                                continue;
                            } else {
                                // href changed, fall through, to create a new link.
                            }
                        } 

                        // create a link to an URL object.
                        String u = normalizeURL(href);
                        NodeList nodeLinkedUrls = get(cloud, relatedUrls, "url", u);
                        Node url;
                        if (nodeLinkedUrls.isEmpty()) {
                            url = getUrlNode(cloud, u, a);
                        } else {
                            url = nodeLinkedUrls.getNode(0).getNodeValue("urls");
                        }
                        usedUrls.add(new Entry(id, url));
                        RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), url.getNodeManager(), "idrel");
                        Relation newIdRel = rm.createRelation(editedNode, url);
                        newIdRel.setStringValue("id", id);
                        newIdRel.setStringValue("class", klass);
                        newIdRel.commit();


                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    a.removeAttribute("href");
                    a.removeAttribute("title");
                    a.removeAttribute("target");
                    a.removeAttribute("class");
                }

            }
            // ready handling links. Now clean up unused links.
            log.debug("Cleaning dangling idrels");
            cleanDanglingIdRels(relatedImages,   usedImages,   "images");
            cleanDanglingIdRels(relatedUrls,     usedUrls,     "urls");
            cleanDanglingIdRels(relatedAttachments, usedAttachments, "attachments");
            cleanDanglingIdRels(relatedTexts, usedTexts, texts.getName());
            cleanDanglingIdRels(relatedBlocks, usedBlocks, "blocks");
        }


        return xml;

    }
    /**
     * At the end of stage 2 of parseKupu all relations are removed which are not used any more, using this function.
     */
    protected void cleanDanglingIdRels(NodeList clusterNodes, List<Map.Entry<String, Node>> usedNodes, String type) {
       NodeIterator i = clusterNodes.nodeIterator();
       while(i.hasNext()) {
           Node clusterNode = i.nextNode();
           Node node = clusterNode.getNodeValue(type);
           Node idrel = clusterNode.getNodeValue("idrel");
           String id = idrel.getStringValue("id");
           if (! usedNodes.contains(new Entry(idrel.getStringValue("id"), node))) {
               if (log.isDebugEnabled()) {
                   log.debug(" " + node + " was not used! id:" + idrel.getStringValue("id"));
               }
               if (idrel == null) {
                   log.debug("Idrel returned null from " + clusterNode + " propbably deleted already in previous cleandDanglingIdRels");
               } else {
                   if (idrel.mayDelete()) {
                       log.service("Removing unused idrel " + id + "-> " + type + " " + node.getNumber());
                       idrel.delete(true);
                   } else {
                       log.service("Could not remove unused idrel " + id + "-> " + type + " " + node.getNumber());;
                   }
               }
           }
       }
    }




    /**
     * Receives Docbook XML, and saves as MMXF. Docbook is more powerfull as MMXF so this
     * transformation will not be perfect. It is mainly meant for MMBase documentation.
     */
    protected Document parseDocBook(Node editedNode, Document source) {
        Cloud cloud = editedNode.getCloud();
        java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/docbook2pseudommxf.xslt");
        DOMResult result = new DOMResult();
        Map params = new HashMap();
        params.put("cloud", cloud);
        try {
            XSLTransformer.transform(new DOMSource(source), u, result, params);
        } catch (javax.xml.transform.TransformerException te) {
            log.error(te);
        }
        Document pseudoMmxf = result.getNode().getOwnerDocument();
        // should follow some code to remove and create cross-links, images etc.

        NodeManager urls = cloud.getNodeManager("urls");
        NodeList relatedUrls          = Queries.getRelatedNodes(editedNode, urls ,  "idrel", "destination", "id", null);
        NodeList usedUrls             = cloud.createNodeList();

        // now find all <a href tags in the pseudo-mmxf, and fix them.
        org.w3c.dom.NodeList nl = pseudoMmxf.getElementsByTagName("a");
        for (int j = 0 ; j < nl.getLength(); j++) {
            Element a = (Element) nl.item(j);
            String href = a.getAttribute("href");
            Node url = getUrlNode(cloud, href, a);
            String id = "_" + indexCounter++;
            a.setAttribute("id", id);
            RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), url.getNodeManager(), "idrel");
            Relation newIdRel = rm.createRelation(editedNode, url);
            newIdRel.setStringValue("id", id);
            newIdRel.commit();
            a.removeAttribute("href");

        }

        return pseudoMmxf;
    }

    private String toString(Object value) {
        if (value instanceof Document) {
            return XMLWriter.write((Document) value, false, true);
        } else {
            return "" + value;
        }
    }

    // javadoc inherited
    public Object process(Node node, Field field, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Found for setstring " + value.getClass() + " " + toString(value));
        }
        try {
            switch(Modes.getMode(node.getCloud().getProperty(Cloud.PROP_XMLMODE))) {
            case Modes.KUPU: {
                log.debug("Handeling kupu-input: " + toString(value));
                return parseKupu(node, parse(value));
            }
            case Modes.WIKI: {
                log.debug("Handling wiki-input: " + value);
                return  parse(xmlField.transformBack(toString(value)));
            }
            case Modes.DOCBOOK: {
                log.debug("Handling docbook-input: " + value);
                return  parseDocBook(node, parse(value));
            }
            case Modes.FLAT: {
                log.debug("Handling flat-input " + value.getClass() + " " + toString(value));
                return parse(xmlField.transformBack(toString(value)));
            }
            default: {
                // 'raw' xml
                try {
                    return parse(value);
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
                Document doc = setProcessor.parse(rl.getResourceAsStream(argv[0]));
                Node node = null;
                if (argv.length > 1) {
                    CloudContext cc = ContextProvider.getDefaultCloudContext();
                    Cloud cloud = cc.getCloud("mmbase", "class", null);
                    if (argv.length > 2) {
                        cloud.setProperty(Cloud.PROP_XMLMODE, argv[2]);
                    }
                    node = cloud.getNode(argv[1]);
                }
                Document mmxf = setProcessor.parseKupu(node, doc);
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
