package com.finalist.cmsc.services.versioning;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.xml.XMLController;
import com.finalist.cmsc.security.SecurityUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Implementation of the VersioningService.
 * 
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class VersioningServiceMMBaseImpl extends VersioningService {

   private final XMLController xmlController;


   public VersioningServiceMMBaseImpl() {
      xmlController = new XMLController(XMLController.defaultDisallowedRelationTypes, null,
            XMLController.defaultDisallowedTypes, null, null, null);
   }

   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(VersioningServiceMMBaseImpl.class);
   public static final int DEFAULT_MAX_ARCHIVES_NODES = 10;


   /**
    * Add a new version to the 'archive' table. All data is stored as 1 field in
    * XML format.
    * 
    * @param node -
    *           Node to create a version from
    */
   @Override
   public void addVersion(Node node) throws VersioningException {
      Cloud cloud = node.getCloud();
      NodeManager nodeManager = cloud.getNodeManager(ARCHIVE);
      try {
         String data = xmlController.toXml(node, false);
         byte[] bytes = data.getBytes("UTF-8");

         NodeManager manager = cloud.getNodeManager(ARCHIVE);
         NodeQuery query = manager.createQuery();
         SearchUtil.addEqualConstraint(query, manager, ORIGINAL_NODE, node.getNumber());
         SearchUtil.addSortOrder(query, manager, DATE, "UP");
         org.mmbase.bridge.NodeList archiveNodeList = manager.getList(query);

         String formerArchiveXml = null;
         if (archiveNodeList.size() > 0) {
            Node formerYoungestArchive = archiveNodeList.getNode(archiveNodeList.size() - 1);
            formerArchiveXml = new String(formerYoungestArchive.getByteValue(NODE_DATA), "UTF-8");
         }
         if (!data.equals(formerArchiveXml)) {
            Node archive = nodeManager.createNode();
            archive.setByteValue(NODE_DATA, bytes);
            archive.setIntValue(ORIGINAL_NODE, node.getNumber());
            archive.setDateValue(DATE, new Date());
            archive.setIntValue("author_number", SecurityUtil.getUserNode(cloud).getNumber());
            archive.commit();

            String property = PropertiesUtil.getProperty("max.archivenodes");
            int maxArchiveNodes = NumberUtils.toInt(property, DEFAULT_MAX_ARCHIVES_NODES);
            int count = 0;
            // loop cause we do want to remove all obsolete archivenodes in case
            // that the maximum is lowered.
            while (archiveNodeList.size() + 1 - count > maxArchiveNodes) {
               Node archiveNode = archiveNodeList.getNode(count++);
               archiveNode.delete();
            }
         }
         else {
            log.debug("Not archived, because data is equals to latest archivenode.");
         }

      }
      catch (Exception e) {
         log.error("Exception while adding a version for node " + node.getNumber(), e);
         throw new VersioningException("", e);
      }
   }


   /**
    * Restore the data from the archive to the original node. The contents of
    * the fields are replaced, do the nodenumber doesn't change during a
    * restore.
    * 
    * @param archive -
    *           Node with the archived data
    */
   @Override
   public Node restoreVersion(Node archive) throws VersioningException {
      Cloud cloud = archive.getCloud();
      Node node = cloud.getNode(archive.getIntValue(ORIGINAL_NODE));
      byte[] bs = archive.getByteValue(NODE_DATA);
      String string;
      try {
         string = new String(bs, "UTF-8");
         setFromXml(node, string);
         node.commit();
         return node;
      }
      catch (Exception e) {
         log.error("Exception while restoring a version for node " + ((node == null) ? null : node.getNumber()), e);
         throw new VersioningException("", e);
      }
   }


   @Override
   public void removeVersions(Node node) {
      Cloud cloud = node.getCloud();
      NodeManager manager = cloud.getNodeManager(ARCHIVE);
      NodeQuery query = manager.createQuery();
      SearchUtil.addEqualConstraint(query, manager, ORIGINAL_NODE, node.getNumber());
      SearchUtil.addSortOrder(query, manager, DATE, "UP");
      org.mmbase.bridge.NodeList archiveNodeList = manager.getList(query);

      int count = 0;
      while (count < archiveNodeList.size()) {
         Node archiveNode = archiveNodeList.getNode(count++);
         archiveNode.delete();
      }

   }


   private void setFromXml(Node n, String xml) throws ParserConfigurationException, IOException, SAXException,
         VersioningException {

      DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = parser.parse(new InputSource(new StringReader(xml)));
      NodeManager nodeManager = n.getNodeManager();
      String nodeManagerName = nodeManager.getName();
      NodeList nodeManagerTag = document.getElementsByTagName(nodeManagerName);
      if (nodeManagerTag.getLength() == 0) {
         throw new VersioningException("Could not match xml with given node!");
      }
      NodeList fields = nodeManagerTag.item(0).getChildNodes();
      for (int i = 0; i < fields.getLength(); i++) {
         org.w3c.dom.Node field = fields.item(i);
         String name = field.getNodeName();
         if (nodeManager.hasField(name)) {
            Field nodeField = nodeManager.getField(name);
            if (nodeField.getState() == Field.STATE_PERSISTENT && !"number".equals(name) && !"owner".equals(name)) {
               org.w3c.dom.Node data = field.getFirstChild();
               String nodeValue = "";
               if (data != null) {
                  nodeValue = data.getNodeValue();
               }
               if (nodeField.getType() == Field.TYPE_DATETIME) {
                  try {
                     n.setDateValue(name, DateFormat.getDateTimeInstance().parse(nodeValue));
                  }
                  catch (ParseException e) {
                     throw new VersioningException(e);
                  }
               }
               else {
                  n.setStringValue(name, nodeValue);
               }
            }
         }
      }
   }
   public void setPublishVersion(Node node) throws VersioningException {
      Cloud cloud = node.getCloud();
      try {
         String data = xmlController.toXml(node, false);

         NodeManager manager = cloud.getNodeManager(ARCHIVE);
         NodeQuery query = manager.createQuery();
         SearchUtil.addEqualConstraint(query, manager, ORIGINAL_NODE, node.getNumber());
         SearchUtil.addSortOrder(query, manager, DATE, "UP");
         org.mmbase.bridge.NodeList archiveNodeList = manager.getList(query);
         String formerArchiveXml = null;
         for (int i = 0 ; i < archiveNodeList.size() ; i++) {
            Node versionNode = archiveNodeList.getNode(i);
            formerArchiveXml = new String(versionNode.getByteValue(NODE_DATA), "UTF-8");
            if (data.equals(formerArchiveXml)) {
               versionNode.setBooleanValue("publish", true);
            }
            else {
               versionNode.setBooleanValue("publish", false); 
            }
            versionNode.commit();
         }
      }
      catch (Exception e) {
         log.error("Exception while set publish mark on  a version for node " + node.getNumber(), e);
         throw new VersioningException("", e);
      }  
   }
}
