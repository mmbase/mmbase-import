/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext.processors.xml;

import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.util.Entry;
import org.mmbase.richtext.Mmxf;
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.storage.search.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import org.mmbase.util.logging.*;


/**
 * This implements 'Kupu' Mode of {@link MmxfSetString}.
 * @author Michiel Meeuwissen
 * @version $Id$
 */

class Kupu {
    private static final Logger log = Logging.getLoggerInstance(Kupu.class);
    private static final long serialVersionUID = 1L;



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
    private static Set<String> divClasses    = new HashSet<String>(Arrays.asList(new String[] {"float", "note", "left", "right", "intermezzo", "caption", "quote"}));
    private static Set<String> imageClasses  = new HashSet<String>(Arrays.asList(new String[] {"image-inline", "image-left", "image-right", "big"}));
    private static Set<String> flashClasses  = new HashSet<String>(Arrays.asList(new String[] {"image-inline", "image-left", "image-right"}));

    private static Pattern allowedAttributes = Pattern.compile("id|href|src|class|type|height|width");

    private static void copyAllowedAttributes(Element source, Element destination) {
        NamedNodeMap attributes = source.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            org.w3c.dom.Node n = attributes.item(i);
            if (allowedAttributes.matcher(n.getNodeName()).matches()) {
                destination.setAttribute(n.getNodeName(), n.getNodeValue());
            }
        }
    }




    /**
     * First stage of parsing kupu-output. Does nothing with relations, only cleans up to 'mmxf' XML.
     *
     * @param source       XML as received from kupu
     * @param destination  pseudo MMXF which is going to receive it.
     * @param links        This list collects elements representing some kind of link (cross-links, images, attachments, urls). Afterwards these can be compared with actual MMBase objects.
     * @param state        The function is called recursively, and this object remembers the state then (where it was while parsing e.g.).
     */

    private void parse(Element source, Element destination, List<Element> links, ParseState state) {
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
                if (node.getNodeValue() != null) {
                    if (state.mode == MODE_SECTION) {
                        String string = Util.normalizeWhiteSpace(node.getNodeValue()).trim();
                        if (! "".equals(string)) {
                            Text text = destination.getOwnerDocument().createTextNode(string);
                            Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "p");
                            log.debug("Appending to " + destination.getNodeName());
                            destination.appendChild(imp);
                            imp.appendChild(text);
                        }
                    } else {
                        Text text = destination.getOwnerDocument().createTextNode(Util.normalizeWhiteSpace(node.getNodeValue()));
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
                parse((Element) node, destination, links, new ParseState(state.level, MODE_INLINE));
                continue;
            }

            matcher = crossElement.matcher(name);
            if (matcher.matches()) {
                Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "a");
                copyAllowedAttributes((Element) node, imp);
                if (name.equals("div")) {
                    Set<String> cssClasses = Util.getCssClasses(imp.getAttribute("class"), divClasses);
                    if (! cssClasses.contains("float")) {
                        // this is no div of ours (copy/pasting?), ignore it.
                        parse((Element) node, destination, links, new ParseState(state.level, MODE_INLINE));
                        continue;
                    } else {
                        imp.setAttribute("class", "div " + Util.getCssClass(cssClasses));
                    }
                }
                if (state.mode == MODE_SECTION) {
                    Element p = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "p");
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
                    Util.copyChilds((Element) node, imp);

                } else {
                    if ("generated".equals(imp.getAttribute("class"))) {
                        // body was generated by kupu, ignore that, it's only presentation.
                        log.debug("Found generated body, ignoring that");
                    } else {
                        // could only do something for 'a' and 'div', but well, never mind
                        parse((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                    }
                }
                continue;
            }
            if (name.equals("i")) { // produced by FF
                if (node.getFirstChild() != null) { // ignore if empty
                    Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "em");
                    destination.appendChild(imp);
                    parse((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
                continue;
            }
            if (name.equals("b")) { // produced by FF
                if (node.getFirstChild() != null) { // ignore if empty
                    Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "strong");
                    destination.appendChild(imp);
                    parse((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
                continue;
            }
            if (name.equals("br")) { // sigh. Of course br is sillyness, but people want it.
                if (state.mode == MODE_INLINE) {
                    Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "br");
                    destination.appendChild(imp);
                    continue;
                } else {
                    log.warn("Found a br-tag, but not in INLINE mode, ignoring");
                    continue;
                }
            }

            matcher = copyElement.matcher(name);
            if (matcher.matches()) {
                org.w3c.dom.Node firstChild = node.getFirstChild();
                if (firstChild != null && !(firstChild.getNodeType() == org.w3c.dom.Node.TEXT_NODE && firstChild.getNodeValue().equals(""))) { // ignore if empty
                    Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, matcher.group(0));
                    destination.appendChild(imp);
                    copyAllowedAttributes((Element) node, imp);
                    parse((Element) node, imp, links, new ParseState(state.level, MODE_INLINE));
                }
                continue;
            }
            matcher = hElement.matcher(name);
            if (matcher.matches()) {
                if (state.mode != MODE_SECTION) {
                    log.warn("Found a section where it cannot be! (h-tags need to be on root level");
                    // treat as paragraph
                    Element imp = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "p");
                    destination.appendChild(imp);
                    copyAllowedAttributes((Element) node, imp);
                    parse((Element) node, imp, links,  new ParseState(state.level, MODE_INLINE));
                    continue;
                }

                int foundLevel = Integer.parseInt(matcher.group(1));

                log.debug(state.level() + " Found section " + foundLevel + " on " + state.level);
                if (foundLevel > state.level) {
                    // need to create a new state.
                    Element section = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "section");
                    Element h       = destination.getOwnerDocument().createElementNS(Mmxf.NAMESPACE, "h");
                    section.appendChild(h);
                    if (foundLevel == state.level + 1) {
                        parse((Element) node, h, links,  new ParseState(state.level, MODE_INLINE));
                        state.subSections.add(section);
                        ParseState newState = new ParseState(foundLevel, MODE_SECTION, state.offset + 1);
                        parse(source, section, links, newState);
                        state.offset = newState.offset;
                    } else {
                        state.subSections.add(section);
                        ParseState newState = new ParseState(state.level + 1, MODE_SECTION, state.offset);
                        parse(source, section, links, newState);
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
            parse((Element) node, destination, links, new ParseState(state.level, MODE_INLINE));
        }
        if (state.mode == MODE_SECTION) {
            // drop state;
            while(! state.subSections.isEmpty()) {
                destination.appendChild((org.w3c.dom.Node) state.subSections.remove(0));
            }
        }
    }






    final Pattern DIV_ID = Pattern.compile("block_(.*?)_(.*)");


    private String getHref(Element a, Cloud cloud) {
        String href = a.getAttribute("href");
        if ("".equals(href)) {
            // must be an image then.
            // Images are _always_ on the same server.
            String src = a.getAttribute("src");
            try {
                java.net.URI uri = new java.net.URI(src);
                String q = uri.getQuery();
                href  = uri.getPath() + (q != null ? "?" + q : "");
            } catch (java.net.URISyntaxException se) {
                log.warn(se);
                href = src;
            }

        }
        String hrefBefore = href;
        if (! "".equals(href)) {
            if (! href.startsWith("mmbase:")) {
                href = Util.normalizeURL((HttpServletRequest) cloud.getProperty("request"), href);
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
            id = "_" + Util.indexCounter++;
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
        if (! href.startsWith(imageServlet)) {
            log.debug("Not an image because '" + href + "' does not start with '" + imageServlet + "'");
            return false;
        }
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
        String klass = Util.getCssClass(a.getAttribute("class"), imageClasses);;
        String id = a.getAttribute("id");
        usedImages.add(new Entry(id, image));
        NodeList linkedImage = Util.get(cloud, relatedImages, "idrel.id", a.getAttribute("id"));
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

    private boolean handleFlash(String href, Element a, List<Map.Entry<String, Node>> usedFlash,
                                NodeList relatedFlash, Node editedNode) {
        Cloud cloud = editedNode.getCloud();
        HttpServletRequest request = (HttpServletRequest) cloud.getProperty("request");
        NodeManager flashobjects = cloud.getNodeManager("flashobjects");
        String flashIntro = request.getContextPath() + "/mmbase/kupu/mmbase/icons/flash.png?o=";
        if (! href.startsWith(flashIntro)) {
            log.debug("href " + href + " does not start with " + flashIntro + "hence this is not a flash object");
            return false;
        }
        log.debug("Found flash " + href);
        String nodeNumber = href.substring(flashIntro.length());
        Node flash = cloud.getNode(nodeNumber);
        String klass = Util.getCssClass(a.getAttribute("class"), flashClasses);;
        String id = a.getAttribute("id");
        boolean updated = false;
        {
            String heightAttr = a.getAttribute("height");
            if (! "".equals(heightAttr)) {
                int height = Integer.parseInt(heightAttr);
                flash.setValue("height", height);
                updated = true;
            } else {
                log.warn("No height found on " + XMLWriter.write(a, false));
            }
        }
        {
            String widthAttr = a.getAttribute("width");
            if (! "".equals(widthAttr)) {
                int width = Integer.parseInt(widthAttr);
                flash.setValue("width", width);
                updated = true;
            } else {
                log.warn("No width found on " + XMLWriter.write(a, false));
            }
        }
        if (updated) {
            flash.commit();
        }


        usedFlash.add(new Entry(id, flash));
        NodeList linkedFlash = Util.get(cloud, relatedFlash, "idrel.id", a.getAttribute("id"));
        if (! linkedFlash.isEmpty()) {
            // ok, already related!
            log.service("" + flash + " image already correctly related, nothing needs to be done");
            Node idrel = linkedFlash.getNode(0).getNodeValue("idrel");
            if (!idrel.getStringValue("class").equals(klass)) {
                idrel.setStringValue("class", klass);
                idrel.commit();
            }

        } else {
            log.service(" to" + flash + ", creating new relation");
            RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), flashobjects, "idrel");
            Relation newIdRel = rm.createRelation(editedNode, flash);
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
        NodeList linkedAttachment = Util.get(cloud, relatedAttachments, "idrel.id", id);
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
        NodeList linkedText = Util.get(cloud, relatedTexts, "idrel.id", id);
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
        Util.copyChilds(a, blockBody);


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
        String klass = Util.getCssClass(a.getAttribute("class"), divClasses);;
        String id = a.getAttribute("id");
        usedBlocks.add(new Entry(id, block));
        NodeList linkedBlock = Util.get(cloud, relatedBlocks, "idrel.id", id);
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
    Document parse(Node editedNode, Document document) {
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
        parse(body, mmxf, links, new ParseState(0, MODE_SECTION));


        // second stage, handle kupu-links.
        if (editedNode == null) {
            log.warn("Node node given, cannot handle cross-links!!");
        } else {
            Cloud cloud = editedNode.getCloud();
            NodeManager images       = cloud.getNodeManager("images");
            NodeManager flashobjects = cloud.getNodeManager("flashobjects");
            NodeManager attachments = cloud.getNodeManager("attachments");
            NodeManager urls        = cloud.getNodeManager("urls");
            NodeManager blocks      = cloud.getNodeManager("blocks");

            NodeManager texts       = cloud.getNodeManager("object");
            Pattern mmbaseUrl         = Pattern.compile("mmbase://(.*?)/(\\d+)");


            NodeList relatedImages        = Util.getRelatedNodes(editedNode, images);
            List<Map.Entry<String, Node>> usedImages = new ArrayList<Map.Entry<String, Node>>();

            NodeList relatedFlash        = Util.getRelatedNodes(editedNode, flashobjects);
            List<Map.Entry<String, Node>> usedFlash = new ArrayList<Map.Entry<String, Node>>();

            NodeList relatedAttachments   = Util.getRelatedNodes(editedNode, attachments);
            List<Map.Entry<String, Node>> usedAttachments = new ArrayList<Map.Entry<String, Node>>();

            NodeList relatedBlocks        = Util.getRelatedNodes(editedNode, blocks);
            List<Map.Entry<String, Node>> usedBlocks = new ArrayList<Map.Entry<String, Node>>();

            NodeList relatedUrls          = Util.getRelatedNodes(editedNode, urls);
            List<Map.Entry<String, Node>> usedUrls = new ArrayList<Map.Entry<String, Node>>();

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
                    } else if (handleFlash(href, a, usedFlash, relatedFlash, editedNode)) { // found an image!
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

                        NodeList idLinkedUrls = Util.get(cloud, relatedUrls, "idrel.id", id);
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
                        String u = Util.normalizeURL(href);
                        NodeList nodeLinkedUrls = Util.get(cloud, relatedUrls, "url", u);
                        Node url;
                        if (nodeLinkedUrls.isEmpty()) {
                            url = Util.getUrlNode(cloud, u, a);
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
     * At the end of stage 2 of parse all relations are removed which are not used any more, using this function.
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



}
