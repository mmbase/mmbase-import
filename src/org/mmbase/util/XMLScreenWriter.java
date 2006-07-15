/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.IOException;
import java.io.Writer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Writes XML as pretty printed HTML
 *
 * @author Cees Roele
 * @version $Id: XMLScreenWriter.java,v 1.10 2003-05-08 06:09:25 kees Exp $
 */
public class XMLScreenWriter extends XMLBasicReader {

   // logger
   private static Logger log =
      Logging.getLoggerInstance(XMLScreenWriter.class.getName());

   static String tag_color = "#007700";
   static String attribute_color = "#DD0000";
   static String comment_color = "#FF8000";
   static String doctype_color = "#6666CC";

   public XMLScreenWriter(String filename) {
      super(filename);
   }

   public void write(Writer out) throws IOException {
      write(out, document, -1);
   }

   public void write(Writer out, Node node, int level) throws IOException {
      if (node != null) {
         if (node.getNodeType() == Node.COMMENT_NODE) {
            out.write(indent(level));
            out.write(
               "<font color=\""
                  + comment_color
                  + "\">&lt;!--"
                  + node.getNodeValue()
                  + "--&gt;</font><br />\n");
         }
         else
            if (node.getNodeType() == Node.DOCUMENT_NODE) {
               NodeList nl = node.getChildNodes();
               for (int i = 0; i < nl.getLength(); i++) {
                  write(out, nl.item(i), level + 1);
               }
            }
            else
               if (node.getNodeType() == Node.TEXT_NODE) {
                  out.write(node.getNodeValue() + "\n");
               }
               else
                  if (node.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
                     String publicid = ((DocumentType) node).getPublicId();
                     String systemid = ((DocumentType) node).getSystemId();
                     if (!((publicid == null || publicid.equals(""))
                        && (systemid == null || systemid.equals("")))) {
                        out.write(
                           "<font color=\""
                              + doctype_color
                              + "\">&lt;!DOCTYPE "
                              + ((DocumentType) node).getName());
                        if (publicid != null && !publicid.equals("")) {
                           out.write(
                              " PUBLIC \""
                                 + ((DocumentType) node).getPublicId()
                                 + "\"");
                        }
                        if (systemid != null && !systemid.equals("")) {
                           out.write(
                              " \""
                                 + ((DocumentType) node).getSystemId()
                                 + "\"");
                        }
                        out.write("&gt;</font><br />\n");
                     }
                  }
                  else {
                     boolean is_end_node = isEndNode(node);
                     NamedNodeMap nnm = node.getAttributes();
                     if (nnm == null || nnm.getLength() == 0) {
                        out.write(indent(level));
                        out.write(
                           "<font color=\""
                              + tag_color
                              + "\">&lt;"
                              + node.getNodeName()
                              + "&gt;</font>\n");
                     }
                     else {
                        out.write(indent(level));
                        out.write(
                           "<font color=\""
                              + tag_color
                              + "\">&lt;"
                              + node.getNodeName()
                              + "</font>");
                        for (int i = 0; i < nnm.getLength(); i++) {
                           Node attribute = nnm.item(i);
                           out.write(
                              " <font color=\""
                                 + attribute_color
                                 + "\">"
                                 + attribute.getNodeName()
                                 + "=\""
                                 + attribute.getNodeValue()
                                 + "\"</font>");
                           if (i < nnm.getLength() - 1) {
                              out.write(" ");
                           }
                        }
                        out.write(
                           "<font color=\"" + tag_color + "\">&gt;</font>");
                     }
                     NodeList nl = node.getChildNodes();
                     if (!is_end_node) {
                        out.write("<br />\n");
                     }
                     for (int i = 0; i < nl.getLength(); i++) {
                        write(out, nl.item(i), level + 1);
                     }
                     if (!is_end_node) {
                        out.write(indent(level));
                     }
                     out.write(
                        "<font color=\""
                           + tag_color
                           + "\">&lt;/"
                           + node.getNodeName()
                           + "&gt;</font><br />\n");
                  }
      }
   }

   /**
    * @param level Indentation level
    * @return String of hard HTML spaces (&nbsp;) that are multiple of level
    */
   protected String indent(int level) {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < level; i++) {
         buf.append("&nbsp;&nbsp;");
      }
      return buf.toString();
   }

   /**
    * @param node
    * @return Whether the node contains only a text node, or possibly also an attribute node
    */
   protected boolean isEndNode(Node node) {
      int countTextNodes = 0;
      NodeList nl = node.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
         if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
            countTextNodes++;
         }
         else
            if (nl.item(i).getNodeType() == Node.ATTRIBUTE_NODE) {
               // do nothing
            }
            else {
               return false;
            }
      }
      return countTextNodes > 0;
   }
}
