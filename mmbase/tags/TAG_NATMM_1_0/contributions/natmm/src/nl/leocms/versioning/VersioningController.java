/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.versioning;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.FieldList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.RelationList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Edwin van der Elst Date :Nov 6, 2003
 * 
 * @version $Revision: 1.3 $, $Date: 2006-04-21 15:03:13 $
 *  
 */
public class VersioningController {

   protected static Logger log = Logging.getLoggerInstance(VersioningController.class.getName());

   private Cloud cloud;

   /**
	 *  
	 */
   public VersioningController(Cloud c) {
      super();
      cloud = c;
   }

   /**
    * Add a new version to the 'archief' table. All data is stored as 1 field in XML format.
    * 
    * @param node - Node to create a version from
    */
   public int addVersion(Node node) {
      log.debug("addVersion for " + node.getNodeManager().getName() + " " + node.getNumber());
      
      int archiveNumber = -1;
      String originalNode = "" + node.getNumber();
      // This is to prevent paragraphs from being archived twice: once because they are committed by the ew
      // and once because they are related to the article
      long now = System.currentTimeMillis()/1000 ;
      String constraints = "archief.original_node = '" + originalNode + "' AND archief.datum > '"
                           + (now-10) + "' AND archief.datum < '" + (now+2) + "'";

      NodeManager archiveManager = cloud.getNodeManager("archief");
      org.mmbase.bridge.NodeList nlArchive = archiveManager.getList(constraints,null,null);
      if (nlArchive.size()>0) {

         archiveNumber = nlArchive.getNode(0).getNumber();
         log.debug("addVersion: found double archiving of archive node: " + archiveNumber);

      } else {

         try {
            String data = this.toXml(node);
            byte[] bytes = data.getBytes("UTF-8");

            Node archive = archiveManager.createNode();
            archive.setByteValue("node_data", bytes);
            archive.setIntValue("original_node",node.getNumber());
            archive.setIntValue("datum", (int) (System.currentTimeMillis()/1000) );
            archive.commit();
            archiveNumber = archive.getNumber();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return archiveNumber;
   }

   /**
    * Restore the data from the archive to the original node. 
    * The contents of the fields are replaced, do the nodenumber doesn't change during a restore.
    * 
    * @param archive - Node with the archived data
    */
   public String restoreVersion(Node archive) {
      Node node = cloud.getNode( archive.getIntValue("original_node") );
      log.debug("restoreVersion of archive " + archive.getNumber() + " in " + node.getNodeManager().getName() + " " + node.getNumber());
      String errorMsg = "";
      byte[] bs = archive.getByteValue("node_data");
      String string;
      try {
         string = new String(bs,"UTF-8");
         errorMsg = setFromXml(node,string);
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return errorMsg;
   }
   
   private String setFromXml(Node n, String xml) {
      String errorMsg = "";
      try {
         DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document document = parser.parse(new InputSource(new StringReader(xml)));
         NodeList fields = document.getElementsByTagName("field");
         for (int i=0;i<fields.getLength();i++) {
            org.w3c.dom.Node field = fields.item(i);
            String name = field.getAttributes().getNamedItem("name").getNodeValue();
            if (!"number".equals(name) && !"owner".equals(name)) {
               org.w3c.dom.Node data = field.getFirstChild();
               String nodeValue="";
               if (data!=null) {
                  nodeValue=data.getNodeValue();
               }
               n.setStringValue(name, nodeValue);
            }
         }
         n.commit();
         errorMsg += restoreRelations(document, n);

      } catch (Exception e) {
         e.printStackTrace();
      }
      return errorMsg;
   }
   
   private String toXml(Node n) throws Exception {
      StringWriter output;
      try {
         Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
         Element root = document.createElement("node");
         document.appendChild(root);
         NodeManager manager = n.getNodeManager();
         FieldList fieldList = manager.getFields();
         fieldList.sort();
         FieldIterator fieldIterator = fieldList.fieldIterator();
         while (fieldIterator.hasNext()) {
            Field field = fieldIterator.nextField();
            String fieldName = field.getName();

            if (field.getState() == Field.STATE_PERSISTENT) {
               String val = n.getStringValue(fieldName);
               Element element = document.createElement("field");
               element.setAttribute("name", fieldName);
               element.appendChild(document.createTextNode(val));
               root.appendChild(element);
            }
         }

         saveRelations(document, root, n, "related", "attachments");
         saveRelations(document, root, n, "posrel",  "images");
         saveRelations(document, root, n, "posrel",  "link");

         String builderName = n.getNodeManager().getName();
         if (builderName.equals("artikel")) {
            RelationList relatedParagraphs = n.getRelations("posrel","paragraaf");
            for (int i=0; i<relatedParagraphs.size(); i++) {
               Relation relToParagraph = relatedParagraphs.getRelation(i);
               int destination = addVersion(relToParagraph.getDestination());

               Element relationElement = document.createElement("relation");
               root.appendChild(relationElement);

               relationElement.setAttribute("destination", "" + destination);
               relationElement.setAttribute("dtype", "paragraaf");
               relationElement.setAttribute("role", "posrel");
               relationElement.setAttribute("pos", relToParagraph.getStringValue("pos"));
            }
         }

         Transformer transformer = TransformerFactory.newInstance().newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         output = new StringWriter();
         transformer.transform(new DOMSource(document), new StreamResult(output));
      } catch (Exception e) {
         e.printStackTrace();
         throw new Exception(e);
      }
      return output.toString();
   }

   private Element saveRelations(Document document, Element root, Node n, 
                                        String relationManager, String nodeManager) throws Exception {

      RelationList relatedItems = n.getRelations(relationManager,nodeManager);
      for (int i=0; i<relatedItems.size(); i++) {
         Relation relTo = relatedItems.getRelation(i);

         Element relationElement = document.createElement("relation");
         root.appendChild(relationElement);
         relationElement.setAttribute("destination", "" + relTo.getDestination().getNumber());
         log.debug("saveRelations from " + n.getNumber() + " to " + relTo.getDestination().getNumber());
         relationElement.setAttribute("dtype", nodeManager);
         relationElement.setAttribute("role", relationManager);
         if ("posrel".equals(relationManager)) {
            relationElement.setAttribute("pos", relTo.getStringValue("pos"));
         }
         if ("pos2rel".equals(relationManager)) {
            relationElement.setAttribute("pos1", relTo.getStringValue("pos1"));
            relationElement.setAttribute("pos2", relTo.getStringValue("pos2"));
         }
      }
      return root;
   }

   private void deleteRelations(Node n, String relationManager, String nodeManager) throws Exception {
      RelationList relatedItems = n.getRelations(relationManager,nodeManager);
      for (int i=0;i<relatedItems.size();i++) {
         Relation relation = relatedItems.getRelation(i);
         relation.delete(true);
      }
   }

   private String restoreRelations(Document document, Node n) throws Exception {
     log.debug("restoreRelations " + n.getNumber());

     String errorMsg = "";

      org.w3c.dom.NodeList relations = document.getElementsByTagName("relation");
      ArrayList paragraphs = new ArrayList();

      for (int i=0;i<relations.getLength();i++) {
         org.w3c.dom.Node relation = relations.item(i);
         String dtype = relation.getAttributes().getNamedItem("dtype").getNodeValue();
         if ("paragraaf".equals(dtype)) {
            String dest = relation.getAttributes().getNamedItem("destination").getNodeValue();
            if (cloud.hasNode(dest)) {
               String originalNode = cloud.getNode(dest).getStringValue("original_node");
               paragraphs.add(originalNode);
            }
         }
      }

      RelationList relatedItems = n.getRelations("posrel","paragraaf");
      for (int i=0;i<relatedItems.size();i++) {
         Relation relation = relatedItems.getRelation(i);
         Node paragraph = relation.getDestination();
         //log.debug("test "+paragraph.getNumber());
         if (!paragraphs.contains(""+paragraph.getNumber())) {
            paragraph.delete(true);
            //log.debug("del par");
         }
         else {
            relation.delete(true);
            //log.debug("del rel");
         }
      }
      deleteRelations(n, "related", "attachments");
      deleteRelations(n, "posrel",  "images");
      deleteRelations(n, "posrel",  "link");

      String stype = n.getNodeManager().getName();
      for (int i=0;i<relations.getLength();i++) {
         org.w3c.dom.Node relation = relations.item(i);
         String role = relation.getAttributes().getNamedItem("role").getNodeValue();
         String dest = relation.getAttributes().getNamedItem("destination").getNodeValue();
         String dtype = relation.getAttributes().getNamedItem("dtype").getNodeValue();
         if (!cloud.hasNode(dest)) {
            errorMsg += "Not found destination node " + dest + "\n";
         }
         else {
            Node destNode = cloud.getNode(dest);
            if ("archief".equals(destNode.getNodeManager().getName())) {
               Node archive = destNode;
               dest = archive.getStringValue("original_node");
               if (!cloud.hasNode(dest)) {
                  errorMsg += "Not found node " + dest + " for restore\n";
                  destNode = cloud.getNodeManager(dtype).createNode();
               }
               else {
                  destNode = cloud.getNode(dest);
               }
               byte[] bs = archive.getByteValue("node_data");
               String string;
               string = new String(bs,"UTF-8");
               errorMsg += setFromXml(destNode,string);
               destNode.commit();
               dtype = destNode.getNodeManager().getName();
            }
            RelationManager relationManager = cloud.getRelationManager(stype,dtype,role);
            Relation relationNode = cloud.getNode(n.getNumber()).createRelation(destNode, relationManager);
            if ("posrel".equals(role)) {
               relationNode.setStringValue("pos",relation.getAttributes().getNamedItem("pos").getNodeValue());
            }
            if ("pos2rel".equals(role)) {
               relationNode.setStringValue("pos1",relation.getAttributes().getNamedItem("pos1").getNodeValue());
               relationNode.setStringValue("pos2",relation.getAttributes().getNamedItem("pos2").getNodeValue());
            }
            relationNode.commit();
         }
      }
      return errorMsg;
   }
}

/*
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/04/14 16:07:32  henk
 * Versioning on preCommit
 *
 * Revision 1.1  2006/03/05 21:43:59  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.2  2003/11/07 10:39:04  edwin
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/06 15:50:39  edwin
 * all code for versioning
 *
*/
