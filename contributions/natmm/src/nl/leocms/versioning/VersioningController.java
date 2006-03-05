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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Edwin van der Elst Date :Nov 6, 2003
 * 
 * @version $Revision: 1.1 $, $Date: 2006-03-05 21:43:59 $
 *  
 */
public class VersioningController {

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
   public void addVersion(Node node) {
      NodeManager nodeManager = cloud.getNodeManager("archief");
      try {
         String data = VersioningController.toXml(node);
         byte[] bytes = data.getBytes("UTF-8");

         Node archive = nodeManager.createNode();
         archive.setByteValue("node_data", bytes);
         archive.setIntValue("original_node",node.getNumber());
         archive.setIntValue("datum", (int) (System.currentTimeMillis()/1000) );
         archive.commit();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Restore the data from the archive to the original node. 
    * The contents of the fields are replaced, do the nodenumber doesn't change during a restore.
    * 
    * @param archive - Node with the archived data
    */
   public void restoreVersion(Node archive) {
      Node node = cloud.getNode( archive.getIntValue("original_node") );
      byte[] bs = archive.getByteValue("node_data");
      String string;
      try {
         string = new String(bs,"UTF-8");
         setFromXml(node,string);
         node.commit();
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
   }
   
   private static void setFromXml(Node n, String xml) {
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
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   private static String toXml(Node n) throws Exception {
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

}

/*
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2003/11/07 10:39:04  edwin
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/06 15:50:39  edwin
 * all code for versioning
 *
*/