package com.finalist.cmsc.repository.xml;

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.mmapps.commons.bridge.NodeFieldComparator;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;

/**
 * @author Nico Klasens
 */
public class XMLController {

   private static final Logger log = Logging.getLoggerInstance(XMLController.class.getName());

   public final static List<String> defaultDisallowedTypes = new ArrayList<String>();
   public final static List<String> defaultDisallowedRelationTypes = new ArrayList<String>();
   static {
      defaultDisallowedTypes.add(RepositoryUtil.CONTENTCHANNEL);
      defaultDisallowedTypes.add(RepositoryUtil.CONTENTELEMENT);
      defaultDisallowedTypes.add(SecurityUtil.USER);

      defaultDisallowedTypes.add("layout");
      defaultDisallowedTypes.add("portletdefinition");
      defaultDisallowedTypes.add("view");
      defaultDisallowedTypes.add("stylesheet");
      defaultDisallowedTypes.add("portlet");

      defaultDisallowedRelationTypes.add(RepositoryUtil.CREATIONREL);
      defaultDisallowedRelationTypes.add(RepositoryUtil.DELETIONREL);
      defaultDisallowedRelationTypes.add(SecurityUtil.ROLEREL);
      defaultDisallowedRelationTypes.add("navrel");
      defaultDisallowedRelationTypes.add("namedrel");
   }

   private List<String> disallowedTypes;
   private List<String> allowedTypes;

   private List<String> disallowedRelationTypes;
   private List<String> allowedRelationTypes;

   private List<String> disallowedFields;
   private List<String> allowedFields;


   public XMLController(List<String> disallowedRelationTypes, List<String> allowedRelationTypes,
         List<String> disallowedTypes, List<String> allowedTypes, List<String> disallowedFields,
         List<String> allowedFields) {

      this.disallowedRelationTypes = setypList(disallowedRelationTypes);
      this.allowedRelationTypes = setypList(allowedRelationTypes);

      this.disallowedTypes = setypList(disallowedTypes);
      this.allowedTypes = setypList(allowedTypes);

      this.disallowedFields = setypList(disallowedFields);
      this.allowedFields = setypList(allowedFields);
   }


   private List<String> setypList(List<String> allowedRelationTypes) {
      if (allowedRelationTypes == null) {
         return new ArrayList<String>();
      }
      else {
         return allowedRelationTypes;
      }
   }


   public String toXmlNumbersOnly(Node node, NodeList nodes, int contentSize) throws Exception {
      try {
         Document document = getDocument();
         toXml(node, document, null, false);

         Element root = document.getDocumentElement();

         Element sizeElement = document.createElement("childcount");
         sizeElement.appendChild(document.createTextNode(String.valueOf(contentSize)));
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
      }
      catch (Exception e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
   }


   public String toXml(Node node) throws Exception {
      try {
         Document document = getDocument();
         toXml(node, document, null);
         return writeXml(document);
      }
      catch (Exception e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
   }


   public String toXml(Node node, boolean addRelations) throws Exception {
      try {
         Document document = getDocument();
         toXml(node, document, null, addRelations);
         return writeXml(document);
      }
      catch (Exception e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
   }


   public String toXml(Node node, NodeList nodes, int contentSize) throws Exception {
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
            nodesSeenButNotProcessed.add(Integer.valueOf(relatedNode.getNumber()));
         }

         NodeIterator nli = nodes.nodeIterator();
         while (nli.hasNext()) {
            Node n = nli.nextNode();
            nodesSeenButNotProcessed.remove(Integer.valueOf(n.getNumber()));
            toXmlNode(n, document, root, true, false, new HashMap<Integer, Node>(), nodesSeenButNotProcessed);
         }

         return writeXml(document);
      }
      catch (Exception e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
   }


   public String toXml(Node node, String nodeName) throws Exception {
      return toXml(node, nodeName, true);
   }


   public String toXml(Node node, String nodeName, boolean addReleations) throws Exception {
      try {
         Document document = getDocument();
         Element channelEl = document.createElement(nodeName);
         document.appendChild(channelEl);
         toXml(node, document, channelEl, addReleations);
         return writeXml(document);
      }
      catch (Exception e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
   }


   public Element toXml(Node node, Document document, Element root) {
      return toXml(node, document, root, true);
   }


   public Element toXml(Node node, Document document, Element root, boolean addRelations) {
      return toXmlNode(node, document, root, addRelations, false, new HashMap<Integer, Node>());
   }


   public Element toXmlNode(Node node, Document document, Element root, boolean addRelations, boolean fieldsAsAttribute) {
      return toXmlNode(node, document, root, addRelations, fieldsAsAttribute, new HashMap<Integer, Node>());
   }


   private Element toXmlNode(Node node, Document document, Element root, boolean addRelations,
         boolean fieldsAsAttribute, Map<Integer, Node> processedNodes) {
      return toXmlNode(node, document, root, addRelations, fieldsAsAttribute, processedNodes, new ArrayList<Integer>());
   }


   private Element toXmlNode(Node node, Document document, Element root, boolean addRelations,
         boolean fieldsAsAttribute, Map<Integer, Node> processedNodes, List<Integer> nodesSeenButNotProcessed) {
      NodeManager manager = node.getNodeManager();
      String managerName = manager.getName();
      if (!TypeUtil.isSystemType(managerName)) {
         Element nodeElement = document.createElement(managerName);
         toXmlFields(node, document, nodeElement, fieldsAsAttribute);
         addExternalUrl(node, document, nodeElement, fieldsAsAttribute);

         processedNodes.put(Integer.valueOf(node.getNumber()), node);

         if (addRelations && !nodesSeenButNotProcessed.contains(Integer.valueOf(node.getNumber()))) {
            RelationManagerList rml = manager.getAllowedRelations((NodeManager) null, null, "DESTINATION");
            RelationManagerIterator rmi = rml.relationManagerIterator();
            while (rmi.hasNext()) {
               RelationManager rm = rmi.nextRelationManager();

               if (isRelationAllowed(rm)) {
                  toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, processedNodes,
                        nodesSeenButNotProcessed);
               }
            }
         }
         processedNodes.remove(Integer.valueOf(node.getNumber()));

         if (root == null) {
            document.appendChild(nodeElement);
         }
         else {
            root.appendChild(nodeElement);
         }
         return nodeElement;
      }
      return null;
   }


   private boolean isRelationAllowed(RelationManager rm) {
      String typeName = rm.getDestinationManager().getName();
      String relationTypeName = rm.getReciprocalRole();

      if (!allowedRelationTypes.isEmpty()) {
         if (allowedRelationTypes.contains(relationTypeName)) {
            if (!allowedTypes.isEmpty()) {
               return allowedTypes.contains(typeName);
            }
            return true;
         }
         return false;
      }

      if (!disallowedRelationTypes.isEmpty()) {
         if (disallowedRelationTypes.contains(relationTypeName)) {
            return false;
         }
         else {
            if (!disallowedTypes.isEmpty()) {
               return !disallowedTypes.contains(typeName);
            }
         }
         return true;
      }

      return isTypeAllowed(typeName);
   }


   private boolean isTypeAllowed(String typeName) {
      if (!allowedTypes.isEmpty()) {
         return allowedTypes.contains(typeName);
      }

      if (!disallowedTypes.isEmpty()) {
         return !disallowedTypes.contains(typeName);
      }
      return true;
   }


   private void addExternalUrl(Node node, Document document, Element nodeElement, boolean fieldsAsAttribute) {

      String url = null;
      String builderName = node.getNodeManager().getName();

      if ("attachments".equals(builderName) || "images".equals(builderName)) {
         url = ResourcesUtil.getServletPath(node, node.getStringValue("number"));
      }
      else {
         if ("urls".equals(builderName)) {
            url = node.getStringValue("url");
         }
         else {
            if (ContentElementUtil.isContentElement(node)) {
               url = ResourcesUtil.getServletPathWithAssociation("content", "/content/*",
                     node.getStringValue("number"), node.getStringValue("title"));
            }
            else {
               if ("page".equals(builderName) || "site".equals(builderName)) {
                  url = node.getStringValue("externalurl");
                  if (StringUtils.isEmpty(url)) {
                     url = ResourcesUtil.getServletPathWithAssociation("content", "/content/*", node
                           .getStringValue("number"), node.getStringValue("title"));
                  }
               }
            }
         }
      }
      if (StringUtils.isNotEmpty(url)) {
         if (fieldsAsAttribute) {
            nodeElement.setAttribute("externalUrl", url);
         }
         else {
            Element element = document.createElement("externalUrl");
            element.appendChild(document.createTextNode(url));
            nodeElement.appendChild(element);
         }
      }

   }


   public void toXmlFields(Node node, Document document, Element nodeElement, boolean fieldsAsAttribute) {
      toXmlFields(node, document, nodeElement, fieldsAsAttribute, false);
   }


   public void toXmlFields(Node node, Document document, Element nodeElement, boolean fieldsAsAttribute,
         boolean relationFields) {

      FieldList fieldList = node.getNodeManager().getFields();
      fieldList.sort();
      FieldIterator fieldIterator = fieldList.fieldIterator();
      while (fieldIterator.hasNext()) {
         Field field = fieldIterator.nextField();
         String fieldName = field.getName();
         if ((field.getState() == Field.STATE_PERSISTENT || field.getState() == Field.STATE_VIRTUAL)
               && !isMMBaseField(fieldName) && isFieldAllowed(node.getNodeManager(), fieldName)) {

            int type = field.getType();

            Object value = null;
            if ("number".equals(fieldName)) {
               value = Integer.valueOf(node.getNumber());
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
                     val = value.toString();
                  }
                  if (value instanceof Integer) {
                     val = Boolean.toString( ((Integer) value) == 1 );
                  }
               }
               else if (Field.TYPE_DATETIME == type) {
                  val = DateFormat.getDateTimeInstance().format(value);
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


   private boolean isFieldAllowed(NodeManager nodeManager, String fieldName) {
      if (!allowedFields.isEmpty()) {
         if (!allowedFields.contains(fieldName)) {
            NodeManager testManager = nodeManager;
            while (testManager != null) {
               if (allowedFields.contains(testManager.getName() + "." + fieldName)) {
                  return true;
               }
               try {
                  testManager = testManager.getParent();
               }
               catch (NotFoundException nfe) {
                  break;
               }
            }
            return false;
         }
         return true;
      }

      if (!disallowedFields.isEmpty()) {
         if (!disallowedFields.contains(fieldName)) {
            NodeManager testManager = nodeManager;
            while (testManager != null) {
               if (disallowedFields.contains(testManager.getName() + "." + fieldName)) {
                  return false;
               }
               try {
                  testManager = testManager.getParent();
               }
               catch (NotFoundException nfe) {
                  break;
               }
            }
            return true;
         }
         return false;
      }

      return true;
   }


   private boolean isMMBaseField(String fieldName) {
      return "owner".equals(fieldName) || "snumber".equals(fieldName) || "dnumber".equals(fieldName)
            || "rnumber".equals(fieldName) || "dir".equals(fieldName) || fieldName.startsWith("_");
   }


   public void toXmlRelations(Node node, Document document, Element nodeElement, RelationManager rm,
         boolean addRelations, boolean fieldsAsAttribute, Map<Integer, Node> processedNodes) {
      toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, processedNodes,
            new ArrayList<Integer>());
   }


   public void toXmlRelations(Node node, Document document, Element nodeElement, RelationManager rm,
         boolean addRelations, boolean fieldsAsAttribute, Map<Integer, Node> processedNodes,
         List<Integer> nodesSeenButNotProcessed) {
      if (rm.hasField("pos")) {
         Comparator<Node> comparator = new NodeFieldComparator("pos");
         toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, comparator, processedNodes,
               nodesSeenButNotProcessed);
      }
      else {
         toXmlRelations(node, document, nodeElement, rm, addRelations, fieldsAsAttribute, null, processedNodes,
               nodesSeenButNotProcessed);
      }
   }


   private void toXmlRelations(Node node, Document document, Element nodeElement, RelationManager rm,
         boolean addRelations, boolean fieldsAsAttribute, Comparator<Node> comparator,
         Map<Integer, Node> processedNodes, List<Integer> parentNodesSeenButNotProcessed) {
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
         nodesSeenButNotProcessed.add(Integer.valueOf(relatedNode.getNumber()));
      }

      RelationIterator rli = rl.relationIterator();
      int skippedChildren = 0;
      while (rli.hasNext()) {
         Relation relation = rli.nextRelation();
         Node relatedNode = relation.getDestination();
         if (processedNodes.containsKey(relatedNode.getNumber())) {
            // Node already processed.. make sure we don't get circular references
            skippedChildren++;
            log.debug("Skipping child " + relatedNode + ", since it was already processed");
         }
         else {
            nodesSeenButNotProcessed.remove(Integer.valueOf(relatedNode.getNumber()));
            toXmlNode(relatedNode, document, nodeElement, addRelations, fieldsAsAttribute, processedNodes,
                  nodesSeenButNotProcessed);
            toXmlFields(relation, document, (Element) nodeElement.getLastChild(), true, true);
            ((Element) nodeElement.getLastChild()).setAttribute("relationname", relatedRole);
         }
      }
      if (skippedChildren > 0) {
         // This node was already done, make sure the client knows about it as
         // well.
         nodeElement.setAttribute("skippedChildCount", Integer.toString(skippedChildren));
      }
   }


   public Document getDocument() throws Exception {
      try {
         return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      }
      catch (ParserConfigurationException e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
      catch (FactoryConfigurationError e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
   }


   public String writeXml(Document document) throws Exception {
      StringWriter output;
      try {
         Transformer transformer = TransformerFactory.newInstance().newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         output = new StringWriter();
         transformer.transform(new DOMSource(document), new StreamResult(output));
      }
      catch (TransformerConfigurationException e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
      catch (IllegalArgumentException e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
      catch (TransformerFactoryConfigurationError e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
      catch (TransformerException e) {
         log.warn(Logging.stackTrace(e));
         throw e;
      }
      return output.toString();
   }
}
