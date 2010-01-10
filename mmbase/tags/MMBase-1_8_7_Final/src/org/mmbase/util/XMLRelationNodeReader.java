/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Vector;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * This class reads a relation node from an exported application.
 * @application Applications
 * @move org.mmbase.util.xml
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class XMLRelationNodeReader extends XMLNodeReader {


   private static final Logger log = Logging.getLoggerInstance(XMLRelationNodeReader.class);

    /**
     * @since MMBase-1.8
     */
    public XMLRelationNodeReader(InputSource is, ResourceLoader path) {
        super(is, path);
    }

   public Vector getNodes(MMBase mmbase) {
      Vector nodes = new Vector();
      Node n1 = document.getFirstChild();
      if (n1.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
         n1 = n1.getNextSibling();
      }
      while (n1 != null) {
         MMObjectBuilder bul = mmbase.getMMObject(n1.getNodeName());
         if (bul == null) {
            log.error(
               "Can't get builder with name: '" + n1.getNodeName() + "'");
         }
         else {
            Node n2 = n1.getFirstChild();
            while (n2 != null) {
               if (n2.getNodeName().equals("node")) {
                  NamedNodeMap nm = n2.getAttributes();
                  if (nm != null) {
                     Node n4 = nm.getNamedItem("owner");
                     MMObjectNode newNode = bul.getNewNode(n4.getNodeValue());
                     try {
                        n4 = nm.getNamedItem("number");
                        int num = Integer.parseInt(n4.getNodeValue());
                        newNode.setValue("number", num);

                        n4 = nm.getNamedItem("snumber");
                        int rnum = Integer.parseInt(n4.getNodeValue());
                        newNode.setValue("snumber", rnum);
                        n4 = nm.getNamedItem("dnumber");
                        int dnum = Integer.parseInt(n4.getNodeValue());
                        newNode.setValue("dnumber", dnum);
                        n4 = nm.getNamedItem("rtype");
                        String rname = n4.getNodeValue();
                        RelDef reldef = mmbase.getRelDef();
                        if (reldef == null) {
                           log.error(
                              "XMLRelationReader -> can't get reldef builder");
                           return null;
                        }
                        // figure out rnumber
                        int rnumber = reldef.getNumberByName(rname);
                        newNode.setValue("rnumber", rnumber);

                        // directionality

                        if (InsRel.usesdir) {
                           n4 = nm.getNamedItem("dir");
                           int dir = 0;
                           if (n4 != null) {
                              String dirs = n4.getNodeValue();
                              if ("unidirectional".equals(dirs)) {
                                 dir = 1;
                              }
                              else
                                 if ("bidirectional".equals(dirs)) {
                                    dir = 2;
                                 }
                                 else {
                                    log.error(
                                       "invalid 'dir' attribute encountered in "
                                          + bul.getTableName()
                                          + " value="
                                          + dirs);
                                 }
                           }
                           if (dir == 0) {
                              MMObjectNode relnode = reldef.getNode(rnumber);
                              if (relnode != null) {
                                 dir = relnode.getIntValue("dir");
                              }
                           }
                           if (dir != 1)
                              dir = 2;
                           newNode.setValue("dir", dir);
                        }

                     }
                     catch (Exception e) {
                        log.error(e);
                        log.error(Logging.stackTrace(e));
                     }
                     Node n5 = n2.getFirstChild();
                     while (n5 != null) {
                        String key = n5.getNodeName();
                        if (n5.getNodeType() == Node.ELEMENT_NODE) {
                           Node n6 = n5.getFirstChild();
                           String value = "";
                           if (n6 != null) {
                               value = n6.getNodeValue(); // needs to be a loop
                           }
                           setValue(bul, newNode, n5, key, value);
                        }
                        n5 = n5.getNextSibling();
                     }
                     nodes.add(newNode);
                  }
               }
               n2 = n2.getNextSibling();
            }
         }
         n1 = n1.getNextSibling();
      }
      return nodes;
   }
}
