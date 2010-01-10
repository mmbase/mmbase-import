/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;

import org.mmbase.util.logging.*;
import org.w3c.dom.*;

/**
 * Reads a contextdepth type of application export configuration file.
 * Such a file conatins paramters for the ContextDepth appliaction export.
 * Parameters exits of a start node, and a maximum depth to which to collect nodes for export.
 * The start node is identified either an alias or a combination of buildername and where clause.
 * This class can be used to easily retrive these parameters.
 *
 * @application Applications
 * @move org.mmbase.util.xml
 * @rename ContextDepthReader
 * @duplicate extend from org.mmbase.util.xml.DocumentReader
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class XMLContextDepthReader {

    // logger
    private static final Logger log = Logging.getLoggerInstance(XMLContextDepthReader.class);

    final Document document;

    /**
     * Creates the Context Depth Reader
     */
    public XMLContextDepthReader(Document doc) {
        document = doc;
    }

    /**
     * Retrieves the depth to which to serach.
     */
    public int getDepth() {
        Node n1 = document.getFirstChild();
        if (n1 != null) {
            Node n2 = n1.getFirstChild();
            while (n2 != null) {
                if (n2.getNodeName().equals("depth")) {
                    Node n3 = n2.getFirstChild();
                    String tmp = n3.getNodeValue();
                    try {
                        return Integer.parseInt(tmp);
                    } catch (Exception e) {
                        return -1;
                    }

                }
                n2 = n2.getNextSibling();
            }
        }
        return -1;
    }

    /**
     * Retrieves the content of the buidler attribute of the startnode.
     */
    public String getStartBuilder() {
        Node n1 = document.getFirstChild();
        if (n1 != null) {
            Node n2 = n1.getFirstChild();
            while (n2 != null) {
                if (n2.getNodeName().equals("startnode")) {
                    Node n3 = n2.getFirstChild();
                    while (n3 != null) {
                        if (n3.getNodeName().equals("builder")) {
                            Node n4 = n3.getFirstChild();
                            return n4.getNodeValue();
                        }
                        n3 = n3.getNextSibling();
                    }
                }
                n2 = n2.getNextSibling();
            }
        }
        return null;
    }

    /**
     * Retrieves the content of the alias attribute of the startnode.
     */
    public String getStartAlias() {
        Node n1 = document.getFirstChild();
        if (n1 != null) {
            Node n2 = n1.getFirstChild();
            while (n2 != null) {
                if (n2.getNodeName().equals("startnode")) {
                    NamedNodeMap nm = n2.getAttributes();
                    if (nm != null) {
                        Node n4 = nm.getNamedItem("alias");
                        if (n4 != null)
                            return n4.getNodeValue();
                    }

                }
                n2 = n2.getNextSibling();
            }
        }
        return null;
    }

    /**
     * Retrieves the content of the where attribute of the startnode.
     */
    public String getStartWhere() {
        Node n1 = document.getFirstChild();
        if (n1 != null) {
            Node n2 = n1.getFirstChild();
            while (n2 != null) {
                if (n2.getNodeName().equals("startnode")) {
                    Node n3 = n2.getFirstChild();
                    while (n3 != null) {
                        if (n3.getNodeName().equals("where")) {
                            Node n4 = n3.getFirstChild();
                            return n4.getNodeValue();
                        }
                        n3 = n3.getNextSibling();
                    }
                }
                n2 = n2.getNextSibling();
            }
        }
        return null;
    }
}
