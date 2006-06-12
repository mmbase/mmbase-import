package com.finalist.cmsc.repository.xml;

import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.mmapps.commons.bridge.NodeFieldComparator;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;

/**
 * @author Nico Klasens
 */
public class XMLController {

   static Logger log = Logging.getLoggerInstance(XMLController.class.getName());

   private static List<String> disallowedTypes = new ArrayList<String>();
   private static List<String> disallowedRelationTypes = new ArrayList<String>();
   static {
       disallowedTypes.add(RepositoryUtil.CONTENTCHANNEL);
       disallowedTypes.add(SecurityUtil.USER);
       
       disallowedRelationTypes.add(RepositoryUtil.CREATIONREL);
       disallowedRelationTypes.add(RepositoryUtil.DELETIONREL);
       disallowedRelationTypes.add(SecurityUtil.ROLEREL);
   }
   
   public static String toXmlNumbersOnly(Node node, NodeList nodes, int contentSize) throws Exception {
        try {
           Document document = getDocument();
           toXml(node, document, null, false);

           Element root = document.getDocumentElement();

           Element sizeElement = document.createElement("childcount");
           sizeElement.appendChild(document.createTextNode(String
                   .valueOf(contentSize)));
           root.appendChild(sizeElement);

           NodeIterator nli = nodes.nodeIterator();
           while (nli.hasNext()) {
               Node n = nli.nextNode();

               Element nodeElement = document.createElement(n.getNodeManager().getName());
               Element nymberElement = document.createElement("number");
               nymberElement.appendChild(document.createTextNode(String.valueOf(n.getNumber())));
               nodeElement.appendChild(nymberElement);

               root.appendChild(nodeElement);
           }

           return writeXml(document);
       } catch (Exception e) {
           e.printStackTrace();
           throw new Exception(e);
       }
  }

   public static String toXml(Node node) throws Exception {
      try {
         Document document = getDocument();
         toXml(node, document, null);
         return writeXml(document);
      } catch (Exception e) {
         e.printStackTrace();
         throw new Exception(e);
      }
   }

   public static String toXml(Node node, boolean addRelations) throws Exception {
      try {
         Document document = getDocument();
         toXml(node, document, null, addRelations);
         return writeXml(document);
      } catch (Exception e) {
         e.printStackTrace();
         throw new Exception(e);
      }
   }
   
   public static String toXml(Node node, NodeList nodes, int contentSize) throws Exception {
      try {
         Document document = getDocument();
         toXml(node, document, null, false);
         
         Element root = document.getDocumentElement();

         Element sizeElement = document.createElement("childcount");
         sizeElement.appendChild(document.createTextNode(String.valueOf(contentSize)));
         root.appendChild(sizeElement);
         
         List<Integer> nodesSeenButNotProcessed = new ArrayList<Integer>();
         NodeIterator nli1 = nodes.nodeIterator();
         while (nli1.hasNext()) {
            Node relatedNode = nli1.nextNode();
            nodesSeenButNotProcessed.add(new Integer(relatedNode.getNumber()));
         }
         
         NodeIterator nli = nodes.nodeIterator();
         while(nli.hasNext()) {
            Node n = nli.nextNode();
            nodesSeenButNotProcessed.remove(new Integer(n.getNumber()));
            toXmlNode(n, document, root, true, false, new HashMap<Integer, Node>(), nodesSeenButNotProcessed);
         }

         return writeXml(document);
      } catch (Exception e) {
         e.printStackTrace();
         throw new Exception(e);
      }
   }
   
   public static String toXml(Node node, String nodeName) throws Exception {
      return toXml(node, nodeName, true);
   }

   public static String toXml(Node node, String nodeName, boolean addReleations) throws Exception {
   	try {
         Document document = getDocument();
         Element channelEl = document.createElement(nodeName);
         document.appendChild(channelEl);
         toXml(node, document, channelEl, addReleations);
         return writeXml(document);
      } catch (Exception e) {
         e.printStackTrace();
         throw new Exception(e);
      }
   }

   
   public static Element toXml(Node node, Document document, Element root) {
      return toXml(node, document, root, true);
   }

   public static Element toXml(Node node, Document document, Element root, boolean addRelations) {
      return toXmlNode(node, document, root, addRelations, false, new HashMap<Integer, Node>());
   }
   
   public static Element toXmlNode(Node node, Document document, Element root, boolean addRelations, boolean fieldsAsAttribute) {
      return toXmlNode(node, document, root, addRelations, fieldsAsAttribute, new HashMap<Integer, Node>());
   }

   private static Element toXmlNode(Node node, Document document, Element root, boolean addRelations, boolean fieldsAsAttribute, HashMap<Integer, Node> processedNodes) {
      return toXmlNode(node, document, root, addRelations, fieldsAsAttribute, processedNodes, new ArrayList<Integer>());
   }
   
   private static Element toXmlNode(Node node, Document document, Element root, boolean addRelations, boolean fieldsAsAttribute, HashMap<Integer, Node> processedNodes, List<Integer> nodesSeenButNotProcessed) {
      NodeManager manager = node.getNodeManager();
      
      Element nodeElement = document.createElement(manager.getName());
      toXmlFields(node, document, nodeElement, fieldsAsAttribute);
      processedNodes.put(new Integer(node.getNumber()), node);

      if (addRelations && !nodesSeenButNotProcessed.contains(new Integer(node.getNumber()))) {
         RelationManagerList rml = manager.getAllowedRelations((NodeManager) null, null, "DESTINATION");
         RelationManagerIterator rmi = rml.relationManagerIterator();
         while (rmi.hasNext()) {
            RelationManager rm = rmi.nextRelationManager();
            
            if (!disallowedRelationTypes.contains(rm.getReciprocalRole())
                    && !disallowedTypes.contains(rm.getDestinationManager().getName())) {
               toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, processedNodes, nodesSeenButNotProcessed);
            }
         }
      }
      processedNodes.remove(new Integer(node.getNumber()));
      
      if (root != null) {
         root.appendChild(nodeElement);
      }
      else {
         document.appendChild(nodeElement);
      }
      return nodeElement;
   }
   
   public static void toXmlFields(Node node, Document document, Element nodeElement, boolean fieldsAsAttribute) {
      toXmlFields(node, document, nodeElement, fieldsAsAttribute, false);
   }

   public static void toXmlFields(Node node, Document document, Element nodeElement, boolean fieldsAsAttribute, boolean relationFields) {

      FieldList fieldList = node.getNodeManager().getFields();
      fieldList.sort();
      FieldIterator fieldIterator = fieldList.fieldIterator();
      while (fieldIterator.hasNext()) {
         Field field = fieldIterator.nextField();
         String fieldName = field.getName();
         if ((field.getState() == Field.STATE_PERSISTENT || field.getState() == Field.STATE_VIRTUAL)
               && !isMMBaseField(fieldName)) {
            
            int type = field.getType();
            
            Object value = null;
            if ("number".equals(fieldName)) {
               value = new Integer(node.getNumber());
               if (relationFields) {
                  fieldName = "relationnumber";
               }
            }
            else {
               value = node.getValue(fieldName);
            }
            
            String val = "";
            if (value != null) {
               if (Field.TYPE_BOOLEAN == type) {
                  if (value instanceof Boolean) {
                     val = "" + ((Boolean) value).booleanValue();
                  }
                  if (value instanceof Integer) {
                     val = "" + (((Integer) value).intValue() == 1);
                  }
               }
               else {
                    val = value.toString();
               }
            }
            if (fieldsAsAttribute) {
               nodeElement.setAttribute(fieldName, val);
            }
            else {
               Element element = document.createElement(fieldName);
               element.appendChild(document.createTextNode(val));
               nodeElement.appendChild(element);
            }
         }
      }
   }

   private static boolean isMMBaseField(String fieldName) {
      return "owner".equals(fieldName) || "snumber".equals(fieldName) 
            || "dnumber".equals(fieldName) || "rnumber".equals(fieldName)
            || "dir".equals(fieldName) || fieldName.startsWith("_");
   }

   public static void toXmlRelations(Node node, Document document, Element nodeElement, RelationManager rm, boolean addRelations, boolean fieldsAsAttribute, HashMap<Integer, Node> processedNodes) {
       toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, processedNodes, new ArrayList<Integer>());
   }
    
   public static void toXmlRelations(Node node, Document document, Element nodeElement, RelationManager rm, boolean addRelations, boolean fieldsAsAttribute, HashMap<Integer, Node> processedNodes, List<Integer> nodesSeenButNotProcessed) {
      if (rm.hasField("pos")) {
         Comparator<Node> comparator = new NodeFieldComparator("pos");
         toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, comparator, processedNodes, nodesSeenButNotProcessed);
      }
      else {
         toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, null, processedNodes, nodesSeenButNotProcessed);
      }
   }

   private static void toXmlRelations(Node node, Document document, Element nodeElement, RelationManager rm, boolean addRelations, boolean fieldsAsAttribute, Comparator<Node> comparator, HashMap<Integer, Node> processedNodes, List<Integer> parentNodesSeenButNotProcessed) {
      String relatedRole = rm.getForwardRole();
      NodeManager destination = rm.getDestinationManager();
      RelationList rl = node.getRelations(relatedRole, destination, "DESTINATION");
      if (comparator != null) {
         Collections.sort(rl, comparator);
      }
      
      List<Integer> nodesSeenButNotProcessed = new ArrayList<Integer>();
      nodesSeenButNotProcessed.addAll(parentNodesSeenButNotProcessed);
      RelationIterator rli1 = rl.relationIterator();
      while (rli1.hasNext()) {
         Relation relation = rli1.nextRelation();
         Node relatedNode = relation.getDestination();
         nodesSeenButNotProcessed.add(new Integer(relatedNode.getNumber()));
      }
      
      RelationIterator rli = rl.relationIterator();
      int skippedChildren = 0;
      while (rli.hasNext()) {
        Relation relation = rli.nextRelation();
        Node relatedNode = relation.getDestination();
         if(!processedNodes.containsKey(new Integer(relatedNode.getNumber()))) {
            nodesSeenButNotProcessed.remove(new Integer(relatedNode.getNumber()));
            toXmlNode(relatedNode, document, nodeElement, addRelations, fieldsAsAttribute, processedNodes, nodesSeenButNotProcessed);
            toXmlFields(relation, document, (Element) nodeElement.getLastChild(), true, true);
            ((Element) nodeElement.getLastChild()).setAttribute("relationname", relatedRole);
         } else {
            // Node already processed.. make sure we don't get circular references
            skippedChildren++;
            log.debug("Skipping child " + relatedNode + ", since it was already processed");
         }
      }
      if(skippedChildren > 0) {
         // This node was already done, make sure the client knows about it as well.
         nodeElement.setAttribute("skippedChildCount", Integer.toString(skippedChildren));
      }
   }

   public static Document getDocument() throws Exception {
       try {
         return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
         throw new Exception(e);
      } catch (FactoryConfigurationError e) {
         e.printStackTrace();
         throw new Exception(e);
      }
   }
   
   public static String writeXml(Document document) throws Exception {
      StringWriter output;
      try {
         Transformer transformer = TransformerFactory.newInstance().newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         output = new StringWriter();
         transformer.transform(new DOMSource(document), new StreamResult(output));
      } catch (TransformerConfigurationException e) {
         e.printStackTrace();
         throw new Exception(e);
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
         throw new Exception(e);
      } catch (TransformerFactoryConfigurationError e) {
         e.printStackTrace();
         throw new Exception(e);
      } catch (TransformerException e) {
         e.printStackTrace();
         throw new Exception(e);
      }
      return output.toString();
   }
}
