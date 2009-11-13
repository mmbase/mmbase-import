/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.security.implementation.cloudcontext.builders.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.xml.sax.*;
import java.io.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A tool to convert between 'cloud' context security and 'xml' context security. Used in /mmbase/security/admin/
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see    org.mmbase.security.implementation.cloudcontext.builders.Contexts
 * @since MMBase-1.7
 */
public class ConvertTool {
    private static final Logger    log = Logging.getLoggerInstance(ConvertTool.class);

    private Class contextAuthentication;
    private NodeManager contextManager;
    private NodeManager groupManager;
    private NodeManager userManager;
    private NodeManager rankManager;
    private RelationManager rankRelationManager;
    private RelationManager groupContainsRelationManager;
    private RelationManager userContainsRelationManager;
    private RelationManager groupGrantsRelationManager;
    private RelationManager userGrantsRelationManager;
    private RelationManager allowedRelationManager;

    private StringBuffer result = new StringBuffer();

    public ConvertTool(Cloud cloud) throws ClassNotFoundException {
        contextManager = cloud.getNodeManager("mmbasecontexts");
        groupManager = cloud.getNodeManager("mmbasegroups");
        userManager = cloud.getNodeManager("mmbaseusers");
        rankManager = cloud.getNodeManager("mmbaseranks");
        rankRelationManager = cloud.getRelationManager(userManager, rankManager, "rank");
        groupContainsRelationManager = cloud.getRelationManager(groupManager, groupManager, "contains");
        userContainsRelationManager = cloud.getRelationManager(groupManager, userManager, "contains");

        groupGrantsRelationManager = cloud.getRelationManager(contextManager, groupManager, "grants");
        userGrantsRelationManager  = cloud.getRelationManager(contextManager, userManager, "grants");

        allowedRelationManager  = cloud.getRelationManager(contextManager, contextManager, "allowed");

        contextAuthentication = Class.forName("org.mmbase.security.implementation.context.ContextAuthentication");
    }

    org.mmbase.bridge.Node getNode(NodeManager nm, String name, String fieldName) {
        NodeQuery q = nm.createQuery();
        Queries.addConstraints(q, fieldName + " = '" + name + "'");
        org.mmbase.bridge.NodeList nl = nm.getList(q);
        if (nl.size() > 0) {
            return nl.getNode(0);
        } else {
            return null;
        }

    }

    org.mmbase.bridge.Node getNode(NodeManager nm, String name) {
        return getNode(nm, name, "name");
    }

    void log(String message) {
        log.info(message);
        result.append(message).append("\n");
    }

    public String getResult() {
        return result.toString();
    }


    /**
     * Writes the current configuration to an XML file
     */

    public Document writeXml(File file) {
        throw new UnsupportedOperationException("not yet implemented");
    }




    /**
     * Extends the current configuration with an XML file.
     */
    public Document readXml(File file) throws SAXException, IOException {

        InputSource in = new InputSource(new FileInputStream(file));
        Document document = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(true, /* validate */
                                                                                  new org.mmbase.util.xml.ErrorHandler(false, 0), /* don't log, throw exception if not valid */
                                                                                  new org.mmbase.util.xml.EntityResolver(true, contextAuthentication) /* validate */
                                                                                  ).parse(in);


        log("Creating all non-existing contextes.");
        NodeList contexts = document.getElementsByTagName("context");

        for (int i = 0; i < contexts.getLength(); i ++) {
            Node node = contexts.item(i);
            String name = node.getAttributes().getNamedItem("name").getNodeValue();
            if (getNode(contextManager, name) == null) {
                org.mmbase.bridge.Node n = contextManager.createNode();
                n.setStringValue("name", name);
                n.setStringValue("description", "imported from " + file);
                n.commit();
                log("Created a context '" + name + "'");
            }

        }



        log("Creating all non-existing groups.");
        NodeList groups = document.getElementsByTagName("group");

        for (int i = 0; i < groups.getLength(); i ++) {
            Node node = groups.item(i);
            String name = node.getAttributes().getNamedItem("name").getNodeValue();
            if (getNode(groupManager, name) == null) {
                org.mmbase.bridge.Node n = groupManager.createNode();
                n.setStringValue("name", name);
                n.setStringValue("description", "imported from " + file);
                n.commit();
                log("Created a group '" + name + "'");
            }

        }


        log("Creating all non-existing users.");
        NodeList users = document.getElementsByTagName("user");

        for (int i = 0; i < users.getLength(); i ++) {
            Node node = users.item(i);
            NamedNodeMap nnm = node.getAttributes();
            String name    = nnm.getNamedItem("name").getNodeValue();
            String context = nnm.getNamedItem("context").getNodeValue();

            if (getNode(userManager, name, Users.FIELD_USERNAME) == null) {

                Node identify = node.getFirstChild();
                if (identify != null && (! (identify instanceof Element))) {
                    identify = identify.getNextSibling();
                }
                if (identify == null) continue;

                String password = identify.getFirstChild().getNodeValue();
                String rank     = identify.getAttributes().getNamedItem("rank").getNodeValue();

                org.mmbase.bridge.Node defaultContext = getNode(contextManager, context);

                log("Default context: " + defaultContext.getStringValue("name"));


                org.mmbase.bridge.Node n = userManager.createNode();
                n.setStringValue(Users.FIELD_USERNAME, name);
                n.setStringValue(Users.FIELD_PASSWORD, password);
                n.setIntValue(Users.FIELD_STATUS, 1);

                n.commit();
                n.setNodeValue(Users.FIELD_DEFAULTCONTEXT, defaultContext);
                n.commit();

                org.mmbase.bridge.Node rankNode = getNode(rankManager, rank);
                if (rankNode != null) {
                    n.createRelation(rankNode, rankRelationManager).commit();
                }
                log("Created a user '" + name + "' (" + rank + ")");
            }

        }


        log("Adding group structure");
        for (int i = 0; i < groups.getLength(); i ++) {
            Node group = groups.item(i);
            org.mmbase.bridge.Node g = getNode(groupManager, group.getAttributes().getNamedItem("name").getNodeValue());
            log("found " + group.getChildNodes().getLength() + " for " + g.getStringValue("name"));
            Node contains = group.getFirstChild();
            while (contains != null) {
                if (contains instanceof Element && contains.getNodeName().equals("contains")) {
                    String type = contains.getAttributes().getNamedItem("type").getNodeValue();
                    if (type.equals("group")) {
                        org.mmbase.bridge.Node otherGroup = getNode(groupManager, contains.getAttributes().getNamedItem("named").getNodeValue());
                        g.createRelation(otherGroup, groupContainsRelationManager).commit();
                    } else if (type.equals("user")) {
                        org.mmbase.bridge.Node u = getNode(userManager, contains.getAttributes().getNamedItem("named").getNodeValue(), Users.FIELD_USERNAME);
                        g.createRelation(u, userContainsRelationManager).commit();
                    }
                }
                contains = contains.getNextSibling();
            }

        }



        log("Adding rights");

        for (int i = 0; i < contexts.getLength(); i ++) {
            Node context = contexts.item(i);
            org.mmbase.bridge.Node c = getNode(contextManager, context.getAttributes().getNamedItem("name").getNodeValue());
            log("found " + context.getChildNodes().getLength() + " for context " + c.getStringValue("name"));
            Node operation = context.getFirstChild();
            while (operation != null) {
                if (operation instanceof Element && operation.getNodeName().equals("operation")) {
                    String type = operation.getAttributes().getNamedItem("type").getNodeValue();
                    log("found operation '" + type + "'");
                    Node grant = operation.getFirstChild();
                    while (grant != null) {
                        try {
                            if (grant instanceof Element && grant.getNodeName().equals("grant")) {
                                Node groupAttribute = grant.getAttributes().getNamedItem("group");
                                if (groupAttribute != null && groupAttribute.getNodeValue() != null) {
                                    org.mmbase.bridge.Node g = getNode(groupManager, groupAttribute.getNodeValue());
                                    Relation r = c.createRelation(g, groupGrantsRelationManager);
                                    r.setStringValue("operation", type);
                                    r.commit();
                                }
                                Node userAttribute = grant.getAttributes().getNamedItem("user");
                                if (userAttribute != null && userAttribute.getNodeValue() != null) {
                                    org.mmbase.bridge.Node u = getNode(userManager, userAttribute.getNodeValue(), Users.FIELD_USERNAME);
                                    Relation r = c.createRelation(u, userGrantsRelationManager);
                                    r.setStringValue("operation", type);
                                    r.commit();
                                }

                            }
                        } catch (Exception e) {
                            log("Ignored " + type  + " because " + e.getMessage());
                        }
                        grant = grant.getNextSibling();
                    }
                }
                try {
                    if (operation instanceof Element && operation.getNodeName().equals("possible")) {
                        org.mmbase.bridge.Node otherContext = getNode(contextManager, operation.getAttributes().getNamedItem("context").getNodeValue());
                        log("found allowed '" + otherContext.getStringValue("name") + "'");
                        c.createRelation(otherContext, allowedRelationManager).commit();
                    }
                } catch (Exception e) {
                    log("Ignored because " + e.getMessage());
                }
                operation = operation.getNextSibling();
            }

        }



        return document;

    }
}
