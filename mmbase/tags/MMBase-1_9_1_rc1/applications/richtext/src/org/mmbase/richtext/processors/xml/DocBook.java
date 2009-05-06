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
import org.mmbase.util.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * This implements 'DocBook' Mode of {@link MmxfSetString}.
 * @todo EXPERIMENTAL
 * @author Michiel Meeuwissen
 * @version $Id: DocBook.java,v 1.2 2008-03-25 18:00:14 michiel Exp $
 */

class DocBook {
    private static final Logger log = Logging.getLoggerInstance(DocBook.class);
    private static final long serialVersionUID = 1L;

    /**
     * Receives Docbook XML, and saves as MMXF. Docbook is more powerfull as MMXF so this
     * transformation will not be perfect. It is mainly meant for MMBase documentation.
     */
    Document parse(Node editedNode, Document source) {
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
            Node url = Util.getUrlNode(cloud, href, a);
            String id = "_" + Util.indexCounter++;
            a.setAttribute("id", id);
            RelationManager rm = cloud.getRelationManager(editedNode.getNodeManager(), url.getNodeManager(), "idrel");
            Relation newIdRel = rm.createRelation(editedNode, url);
            newIdRel.setStringValue("id", id);
            newIdRel.commit();
            a.removeAttribute("href");

        }

        return pseudoMmxf;
    }


}
