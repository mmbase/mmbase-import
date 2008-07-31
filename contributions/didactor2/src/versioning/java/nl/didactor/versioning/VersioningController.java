package nl.didactor.versioning;

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
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.RelationList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMObjectNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


/*
 * @javadoc
 */
public class VersioningController
{
  private static Logger log = Logging.getLoggerInstance(VersioningController.class.getName());

  /**
   * Add a new version of paragraph to the 'archives' table. All data and relations to paragraphs is
   * stored as 1 field in XML format.
   *
   * @param node - Node to create a version from
   */
  public static int addParagraphVersion(Node node) {
     Cloud cloud = node.getCloud();
     int archiveNumber = -1;
     String originalNode = "" + node.getNumber();

     log.info("addParagraphVersion: " + originalNode);

     NodeManager nodeManager = cloud.getNodeManager("archives");

     try {
        String data = VersioningController.fromParagraphtoXml(node);
        byte[] bytes = data.getBytes("UTF-8");
        Node archive = nodeManager.createNode();
        archive.setByteValue("node_data", bytes);
        archive.setStringValue("original_node",originalNode);
        archive.setIntValue("archive_date", (int) (System.currentTimeMillis()/1000) );
        archive.setStringValue("archived_by", node.getStringValue("owner"));
        archive.commit();
        archiveNumber = archive.getNumber();
     } catch (Exception e) {
        e.printStackTrace();
     }
     return archiveNumber;
  }

  /**
   * Add a new version of node to the 'archives' table. All data is stored as 1 field in XML format.
   *
   * @param node - Node to create a version from
   */
  public static int addSimpleVersion(Node node) {
     Cloud cloud = node.getCloud();
     int archiveNumber = -1;
     String originalNode = "" + node.getNumber();
     long now = System.currentTimeMillis()/1000 ;
     String constraints = "archives.original_node = '" + originalNode + "' AND archives.archive_date > '"
                          + (now-2) + "' AND archives.archive_date < '" + (now+2) + "'";

     log.info("addSimpleVersion: " + originalNode);

     NodeManager nodeManager = cloud.getNodeManager("archives");

     if (nodeManager.getList(constraints,null,null).size()>0) {
        log.info("addSimpleVersion: found double archiving");
     }
     else {

        try {
           String data = VersioningController.toXml(node);
           byte[] bytes = data.getBytes("UTF-8");
           Node archive = nodeManager.createNode();
           archive.setByteValue("node_data", bytes);
           archive.setStringValue("original_node",originalNode);
           archive.setIntValue("archive_date", (int) (System.currentTimeMillis()/1000) );
           archive.setStringValue("archived_by", node.getStringValue("owner"));
           archive.commit();
           archiveNumber = archive.getNumber();
        } catch (Exception e) {
           e.printStackTrace();
        }
     }
     return archiveNumber;
  }

  /**
   * Add a new version of learnobject to the 'archives' table. All data and relations to paragraphs is
   * stored as 1 field in XML format.
   *
   * @param node - Node to create a version from
   */
  public static int addLOVersion(Node node) {
     Cloud cloud = node.getCloud();
     int archiveNumber = -1;
     String originalNode = "" + node.getNumber();
     long now = System.currentTimeMillis()/1000 ;
     String constraints = "archives.original_node = '" + originalNode + "' AND archives.archive_date > '"
                          + (now-2) + "' AND archives.archive_date < '" + (now+2) + "'";

     log.info("addLOVersion: " + originalNode);

     NodeManager nodeManager = cloud.getNodeManager("archives");

     if (nodeManager.getList(constraints,null,null).size()>0) {
        log.info("addLOVersion: found double archiving");
     }
     else {

        try {
           String data = VersioningController.fromLOtoXml(node);
           byte[] bytes = data.getBytes("UTF-8");
           Node archive = nodeManager.createNode();
           archive.setByteValue("node_data", bytes);
           archive.setStringValue("original_node",originalNode);
           archive.setIntValue("archive_date", (int) (System.currentTimeMillis()/1000) );
           archive.setStringValue("archived_by", node.getStringValue("owner"));
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
  public static String restoreVersion(Node archive) {
     Cloud cloud = archive.getCloud();
     String errorMsg = "";
     log.info("restore node " + archive.getNumber());
     String originalNode = archive.getStringValue("original_node");

     if (!cloud.hasNode(originalNode)) {
        errorMsg = "cloud naven't node for restore";
     }
     else {
        Node node = cloud.getNode( originalNode );

        addLOVersion(node);

        log.info("restored node " + node.getNumber());

        byte[] bs = archive.getByteValue("node_data");
        String string;
        try {
           string = new String(bs,"UTF-8");
           errorMsg = setFromXml(node,string);
        } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
        }
     }
     log.info("error for node " + originalNode + ":\n" + errorMsg);
     return errorMsg;
  }

  private static String restoreFields(Document document, Node n) throws Exception {
     org.w3c.dom.NodeList fields = document.getElementsByTagName("field");
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
     return "";
  }

  private static String deleteRelations(Node n, String relationManager, String nodeManager) throws Exception {
     RelationList relatedItems = n.getRelations(relationManager,nodeManager);
     for (int i=0;i<relatedItems.size();i++) {
        Relation relation = relatedItems.getRelation(i);
        relation.delete(true);
     }
     return "";
  }

  private static String restoreRelations(Document document, Node n) throws Exception {
     Cloud cloud = n.getCloud();
     String errorMsg = "";

     org.w3c.dom.NodeList relations = document.getElementsByTagName("relation");
     ArrayList paragraphs = new ArrayList();

     for (int i=0;i<relations.getLength();i++) {
        org.w3c.dom.Node relation = relations.item(i);
        String dtype = relation.getAttributes().getNamedItem("dtype").getNodeValue();
        if ("paragraphs".equals(dtype)) {
           String dest = relation.getAttributes().getNamedItem("destination").getNodeValue();
           if (cloud.hasNode(dest)) {
              String originalNode = cloud.getNode(dest).getStringValue("original_node");
              paragraphs.add(originalNode);
              log.info("add "+originalNode);
           }
        }
     }

     RelationList relatedItems = n.getRelations("posrel","paragraphs");
     for (int i=0;i<relatedItems.size();i++) {
        Relation relation = relatedItems.getRelation(i);
        Node paragraph = relation.getDestination();
        log.info("test "+paragraph.getNumber());
        if (!paragraphs.contains(""+paragraph.getNumber())) {
           paragraph.delete(true);
           log.info("del par");
        }
        else {
           relation.delete(true);
           log.info("del rel");
        }
     }
     errorMsg += deleteRelations(n,"pos2rel","images");
     errorMsg += deleteRelations(n,"posrel","urls");
     errorMsg += deleteRelations(n,"posrel","attachments");

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
           if ("archives".equals(destNode.getNodeManager().getName())) {
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

  private static String setFromXml(Node n, String xml) {
     String errorMsg = "";
     try {
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new InputSource(new StringReader(xml)));
        errorMsg += restoreFields(document, n);
        n.commit();
        errorMsg += restoreRelations(document, n);
     } catch (Exception e) {
        e.printStackTrace();
     }
     return errorMsg;
  }

  private static String fromLOtoXml(Node n) throws Exception {
     StringWriter output;
     try {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element nodeElement = document.createElement("node");
        document.appendChild(nodeElement);
        saveFields(document, nodeElement, n);
        RelationList relatedParagraphs = n.getRelations("posrel","paragraphs");
        for (int i=0; i<relatedParagraphs.size(); i++) {
           Relation relToParagraph = relatedParagraphs.getRelation(i);
           int destination = addParagraphVersion(relToParagraph.getDestination());

           Element relationElement = document.createElement("relation");
           nodeElement.appendChild(relationElement);

           relationElement.setAttribute("destination", "" + destination);
           relationElement.setAttribute("dtype", "paragraphs");
           relationElement.setAttribute("role", "posrel");
           relationElement.setAttribute("pos", relToParagraph.getStringValue("pos"));
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

  private static String toXml(Node n) throws Exception {
     StringWriter output;
     try {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element nodeElement = document.createElement("node");
        document.appendChild(nodeElement);
        saveFields(document, nodeElement, n);

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

  private static String fromParagraphtoXml(Node n) throws Exception {
     StringWriter output;
     try {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element nodeElement = document.createElement("node");
        document.appendChild(nodeElement);
        saveFields(document, nodeElement, n);
        saveRelations(document, nodeElement, n, "posrel", "urls");
        saveRelations(document, nodeElement, n, "posrel", "attachments");
        saveRelations(document, nodeElement, n, "pos2rel","images");
        saveRelations(document, nodeElement, n, "posrel", "urls");

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

  private static Element saveFields(Document document, Element root, Node n) throws Exception {
     try {
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
     } catch (Exception e) {
        e.printStackTrace();
        throw new Exception(e);
     }
     return root;
  }

  private static Element saveRelations(Document document, Element root, Node n, String relationManager, String nodeManager) throws Exception {

     RelationList relatedItems = n.getRelations(relationManager,nodeManager);
     for (int i=0; i<relatedItems.size(); i++) {
        Relation relTo = relatedItems.getRelation(i);

        Element relationElement = document.createElement("relation");
        root.appendChild(relationElement);
        relationElement.setAttribute("destination", "" + relTo.getDestination().getNumber());
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
}
