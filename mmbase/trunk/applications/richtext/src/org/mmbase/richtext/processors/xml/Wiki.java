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
import org.mmbase.util.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*;
import java.util.*;
import java.util.regex.*;

import org.mmbase.util.logging.*;

/**
 * Setting like wiki. A property of wiki-editing is, that you cannot point more then one idrel to
 * the same anchor. Depending on this, the id's of the idrel can be left unfilled, and in the
 * wiki-text, a user can simple refer to the node-number (or perhaps in later refinement some other
 * id of the node).
 *
 * @author Michiel Meeuwissen
 * @version $Id: Wiki.java,v 1.16 2008-12-08 10:06:08 michiel Exp $
 * @todo something goes wrong if same node relation multiple times.
 */

class Wiki {

    private static final Logger log = Logging.getLoggerInstance(Wiki.class);
    private static final long serialVersionUID = 1L;

    /**
     * Searches in the existsing relations for a relation with the
     * given id
     * @param a The anchor which we are trying to match
     * @param links List of alreayd existing relation objects
     * @param id (of a)
     */
    Node findById(Element a, NodeList links, String fieldName, Set<String> usedIds) {
        String xmlId = a.getAttribute("id");
        NodeIterator ni = links.nodeIterator();
        while (ni.hasNext()) {
            Node relation = ni.nextNode();
            String relId  = relation.getStringValue("id");
            if (log.isDebugEnabled()) {
                log.debug("Id in " + relation.getNumber() + " " + relId + " comparing with '" + xmlId);
            }
            if ("".equals(relId)) {
                // id field in relation object is empty, generate one, supposing that the id in the
                // xml simply refers to a node number.
                Node destination = relation.getNodeValue("dnumber");
                log.debug("Found " + destination);
                if (destination == null) {
                    log.warn("dnumber null in " + relation);
                } else {
                    if (destination.getStringValue("number").equals(xmlId)) {
                        log.debug("Setting relation id of " + relation.getNumber() + " to " + destination.getNumber());
                        String id = generateId(fieldName, "" + destination.getNumber(), usedIds);
                        relation.setStringValue("id", id);
                        relation.commit();
                        a.setAttribute("id", id);
                        log.debug("relation " + relation + " " + relation.getCloud());
                        usedIds.add(id);
                        return relation;
                    }
                }
            } else {
                String decorXmlId = decorateId(xmlId, fieldName);
                String decorRelId = decorateId(relId, fieldName);
                if (relId.equals(xmlId) || decorRelId.equals(decorXmlId)) {
                    log.debug(relId + "==" + xmlId);
                    // relation already exists and corresponds with id in this

                    // fix 'legacy' values.
                    if (! decorXmlId.equals(xmlId)) {
                        a.setAttribute("id", decorXmlId);
                    }
                    if (! decorRelId.equals(relId)) {
                        relation.setStringValue("id", decorRelId);
                        relation.commit();
                    }
                    usedIds.add(decorXmlId);
                    return relation;
                } else {
                    usedIds.add(xmlId);
                    log.debug(relId + "!=" + xmlId);
                }
            }

        }
        return null; // not found
    }

    String prefix(String fieldName) {
        return "n_" + fieldName + "_";
    }

    String generateId(String fieldName, String nodeNumber, Set<String> usedIds) {
        String decoratedId = decorateId(nodeNumber, fieldName);
        String uniqueDecoratedId = decoratedId;
        int seq = 0;
        while (usedIds.contains(uniqueDecoratedId)) {
            uniqueDecoratedId = decoratedId + "-" + (++seq);
        }
        return uniqueDecoratedId;
    }

    static final Pattern DECORATED = Pattern.compile("n_[a-zA-Z]+_(\\d+)(?:_\\d+)?");
    String idToNodeNumber(String id, String fieldName) {
        Matcher matcher = DECORATED.matcher(id);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return id;
        }
    }
    /**
     * Prefixes a node number, because the a node number is not a convenient id. Numbers are already
     * used in the o: xml. (The _same_ numbers).
     */
    String decorateId(String id, String fieldName) {
        return prefix(fieldName) + idToNodeNumber(id, fieldName);
    }

   /**
     * Simply considers the id the node-number, but this could be sophisitcated on.
     */
    Node getNode(Cloud cloud, String id, String fieldName) {
        String nodeNumber = idToNodeNumber(id, fieldName);
        if (cloud.hasNode(nodeNumber)) {
            return cloud.getNode(nodeNumber);
        } else {
            return null;
        }

    }

    private static final Pattern ID_HAVERS = Pattern.compile("a|p|section");

    /**
     * @param editedNode Node that is edited. Anchors will be either changed, or idrels will be
     * created/modified to be in order
     * @param source
     *
     */
    Document parse(Node editedNode, Field field, Document source) {
        Cloud cloud = editedNode.getCloud();

        String fieldName = field.getName();
        Set<String> usedIds = new HashSet<String>();

        // reolve anchors. Allow to use nodenumber as anchor.
        if (log.isDebugEnabled()) {
            log.debug("Resolving " + editedNode + " " + XMLWriter.write(source, true));
        }


        // In a transaction, the query will not return our new nodes.
        // Administrate related idrels ourselves.
        // This is also anticipated in body.tagx
        NodeList createdLinks = (NodeList) cloud.getProperty("createdlinks");
        if (createdLinks == null) {
            createdLinks = cloud.createNodeList();
            NodeManager objects = cloud.getNodeManager("object");
            NodeQuery q = Queries.createRelationNodesQuery(editedNode, objects, "idrel", "destination");
            NodeList links = cloud.getNodeManager("idrel").getList(q);
            createdLinks.addAll(links);
            cleanDuplicateIdRels(createdLinks);
            cloud.setProperty("createdlinks", createdLinks);
        } else {
            log.debug("Found create lnsk in cloud already");
        }

        // search all anchors
        org.w3c.dom.NodeList as = source.getElementsByTagName("*");
        for (int i = 0; i < as.getLength(); i++) {
            Element a = (Element) as.item(i);
            if (! ID_HAVERS.matcher(a.getNodeName()).matches()) continue;
            if (log.isDebugEnabled()) {
                log.debug("Found " + XMLWriter.write(a, true));
            }
            String className = a.getAttribute("class");
            a.removeAttribute("class");
            String id = a.getAttribute("id");
            if ("".equals(id)) continue;
            Node link = findById(a, createdLinks, fieldName, usedIds);
            if (link == null) {

                log.service("No relation found with id '" + id + "'. Implicitely creating one now.");
                Node node = getNode(cloud, id, fieldName);
                if (node != null) {
                    try {
                        Relation newRel = editedNode.createRelation(node, cloud.getRelationManager(editedNode.getNodeManager(), node.getNodeManager(), "idrel"));
                        String decoratedId = generateId(fieldName, id, usedIds);
                        newRel.setStringValue("id", decoratedId);
                        newRel.setStringValue("class", className);
                        newRel.commit();
                        createdLinks.add(newRel);
                        a.setAttribute("id", decoratedId);
                        usedIds.add(decoratedId);
                    } catch (Exception e) {
                        log.warn(e);
                    }
                } else {
                    log.debug("No node found for " + id + "");
                }
            } else {
                link.setStringValue("class", className);
                if (link.isChanged()) {
                    link.commit();
                }
            }

        }



        return source;
    }


    /**
     * Duplicate idrels should not have been created, but perhaps in some old version of this code
     * it could happen. Get it over with, and simply make sure there are none
     */
    private void cleanDuplicateIdRels(NodeList list) {
        Set<String> ids = new HashSet<String> ();
        NodeIterator i = list.nodeIterator();

        while (i.hasNext()) {
            Node n = i.nextNode();
            String id = n.getStringValue("id");
            if (ids.contains(id)) {
                try {
                    n.delete(true);
                    log.info("Removed duplicate id " + id);
                    i.remove();
                } catch (Exception e) {
                    log.warn(e);
                }
            } else {
                ids.add(id);
            }
        }
        log.debug("Found ids " + ids);
    }


}
