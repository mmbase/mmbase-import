package com.finalist.cmsc.taglib.editors;

import java.io.*;
import java.util.*;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;

public class DumpDefaultsTag extends SimpleTagSupport {

   private String path;

   private class DumpingNode {
      private String nodeType;
      private List<DumpingConstraint> childConstraints = new ArrayList<DumpingConstraint>();
      private List<DumpingNode> childNodes = new ArrayList<DumpingNode>();


      public DumpingNode(String nodeType) {
         this.nodeType = nodeType;
      }


      public void addChildConstraint(DumpingConstraint constraint) {
         childConstraints.add(constraint);
      }


      public void addChildNode(DumpingNode node) {
         childNodes.add(node);
      }


      public List<DumpingNode> getChildNodes() {
         return childNodes;
      }


      public List<DumpingConstraint> getChildConstraints() {
         return childConstraints;
      }


      public String getNodeType() {
         return nodeType;
      }


      public boolean meetsChildConstraints(Node node) {
         for (DumpingConstraint dumpingConstraint : childConstraints) {
            if (!dumpingConstraint.meets(node)) {
               return false;
            }
         }
         return true;
      }
   }

   private class DumpingConstraint {
      public final static int OPERATOR_EQUALS = 0;
      private String field;
      private int operator;
      private String value;


      public DumpingConstraint(String field, int operator, String value) {
         this.field = field;
         this.operator = operator;
         this.value = value;
      }


      public boolean meets(Node node) {
         String fieldValue = node.getStringValue(field);
         switch (operator) {
            case OPERATOR_EQUALS:
               return value.equals(fieldValue);
         }
         return false;
      }


      public String getField() {
         return field;
      }


      public int getOperator() {
         return operator;
      }


      public String getValue() {
         return value;
      }
   }


   public void setPath(String path) {
      this.path = path;
   }


   @Override
   public void doTag() throws IOException {

      PageContext ctx = (PageContext) getJspContext();
      List<DumpingNode> dumpingNodes = buildDumpingNodesForDefaults();
      String report = doBackup(dumpingNodes);
      ctx.getOut().write(report);

   }


   private List<DumpingNode> buildDumpingNodesForDefaults() {
      List<DumpingNode> dumpingNodes = new ArrayList<DumpingNode>();

      DumpingNode dnTypedefs = new DumpingNode("typedef");
      DumpingNode dnViews = new DumpingNode("view");
      DumpingNode dnStylesheets = new DumpingNode("stylesheet");
      DumpingNode dnSinglgePortletdefinition = new DumpingNode("portletdefinition");
      DumpingNode dnMultiplePortletdefinition = new DumpingNode("portletdefinition");
      DumpingNode dnPortlet = new DumpingNode("portlet");
      DumpingNode dnPortletparameter = new DumpingNode("portletparameter");
      DumpingNode dnLayout = new DumpingNode("layout");

      dumpingNodes.add(dnTypedefs);
      dumpingNodes.add(dnViews);
      dumpingNodes.add(dnStylesheets);
      dumpingNodes.add(dnSinglgePortletdefinition);
      dumpingNodes.add(dnMultiplePortletdefinition);
      dumpingNodes.add(dnLayout);

      dnPortlet.addChildNode(dnPortletparameter);
      dnPortlet.addChildNode(dnViews);

      dnViews.addChildNode(dnTypedefs);

      dnSinglgePortletdefinition.addChildConstraint(new DumpingConstraint("type", DumpingConstraint.OPERATOR_EQUALS,
            "single"));
      dnSinglgePortletdefinition.addChildNode(dnPortlet);

      dnSinglgePortletdefinition.addChildNode(dnTypedefs);

      dnMultiplePortletdefinition.addChildConstraint(new DumpingConstraint("type", DumpingConstraint.OPERATOR_EQUALS,
            "multiple"));
      dnMultiplePortletdefinition.addChildNode(dnTypedefs);
      dnMultiplePortletdefinition.addChildNode(dnViews);

      dnLayout.addChildNode(dnSinglgePortletdefinition);

      return dumpingNodes;
   }


   private String doBackup(List<DumpingNode> dumpingNodes) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      HashMap<String, HashSet<Node>> backupMap = buildBackupMap(dumpingNodes, cloud);
      String report = exportBackupMap(backupMap, cloud);
      return report;
   }


   private String exportBackupMap(HashMap<String, HashSet<Node>> backupMap, Cloud cloud) {
      StringBuffer report = new StringBuffer();

      // Use manager for Object and Insrel field checking
      NodeManager inselManager = cloud.getNodeManager("insrel");

      for (String key : backupMap.keySet()) {
         HashSet<Node> values = backupMap.get(key);

         report.append(key).append(" ").append(values.size()).append(" nodes. <br/>");

         Calendar cal = Calendar.getInstance();
         long htimestamp = cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100
               + cal.get(Calendar.DAY_OF_MONTH);
         long ltimestamp = cal.get(Calendar.AM_PM) * 120000 + cal.get(Calendar.HOUR) * 10000 + cal.get(Calendar.MINUTE)
               * 100 + cal.get(Calendar.SECOND);
         long timestamp = (htimestamp * 1000000) + ltimestamp;

         StringBuffer sb = new StringBuffer();
         sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
         sb.append("<").append(key).append(" exportsource=\"cmsc file dumper\" timestamp=\"").append(timestamp).append(
               "\">\n");
         for (Node node : values) {
            sb.append("\t<node number=\"").append(node.getNumber()).append("\" owner=\"").append(node.getContext());
            if (node instanceof Relation) {
               Relation relation = (Relation) node;
               sb.append("\" snumber=\"").append(relation.getSource().getNumber());
               sb.append("\" dnumber=\"").append(relation.getDestination().getNumber());
               sb.append("\" rtype=\"").append(key);

               if (relation.getIntValue("dir") == 1) {
                  sb.append("\" dir=\"").append("unidirectional");
               }
               else {
                  sb.append("\" dir=\"").append("bidirectional");
               }
            }
            sb.append("\">\n");

            for (FieldIterator fi = node.getNodeManager().getFields().fieldIterator(); fi.hasNext();) {
               Field field = fi.nextField();
               if (!inselManager.hasField(field.getName())
                     && (field.getState() == Field.STATE_PERSISTENT || field.getState() == Field.STATE_SYSTEM)) {
                  String fieldName = field.getName();
                  if (!node.isNull(fieldName)) {
                     sb.append("\t\t<").append(fieldName).append(">");
                     sb.append(node.getStringValue(fieldName));
                     sb.append("</").append(fieldName).append(">\n");
                  }
               }
            }
            sb.append("\t</node>\n");
         }
         sb.append("</" + key + ">");

         FileOutputStream fos = null;
         try {
            String fileName = path + System.getProperty("file.separator") + key + ".xml";
            File fileObject = new File(fileName);
            File folder = fileObject.getParentFile();
            if (!folder.exists()) {
               if (!folder.mkdirs()) {
                  report.append("Failed to create directory '" + fileName + "'<br/>");
               }
            }
            fos = new FileOutputStream(fileName);
            fos.write(sb.toString().getBytes());
         }
         catch (IOException e) {
            report.append("Unable to create backup, " + e.getMessage() + "<br/>");
         }
         finally {
            if (fos != null) {
               try {
                  fos.close();
               }
               catch (IOException e) {
                  report.append("Unable to close backup, " + e.getMessage() + "<br/>");
               }
            }
         }
      }

      return report.toString();
   }


   private HashMap<String, HashSet<Node>> buildBackupMap(List<DumpingNode> dumpingNodes, Cloud cloud) {
      HashMap<String, HashSet<Node>> backupMap = new HashMap<String, HashSet<Node>>();
      for (DumpingNode dumpingNode : dumpingNodes) {
         buildBackupMapNode(cloud, backupMap, dumpingNode);
      }

      return backupMap;
   }


   private void buildBackupMapNode(Cloud cloud, HashMap<String, HashSet<Node>> backupMap, DumpingNode dumpingNode) {
      NodeList nodeList = cloud.getNodeManager(dumpingNode.getNodeType()).getList(null, null, null);

      for (NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         addNodeToBackup(backupMap, node, dumpingNode);
      }
   }


   private void addNodeToBackup(HashMap<String, HashSet<Node>> backupMap, Node node, DumpingNode dumpingNode) {
      String type = node.getNodeManager().getName();
      HashSet<Node> set = backupMap.get(type);
      if (set == null) {
         set = new HashSet<Node>();
         backupMap.put(type, set);
      }

      set.add(node);

      if (dumpingNode.meetsChildConstraints(node)) {
         // add child nodes
         for (DumpingNode childDumpingNode : dumpingNode.getChildNodes()) {
            RelationList relationList = node.getRelations(null, childDumpingNode.getNodeType());
            for (RelationIterator ri = relationList.relationIterator(); ri.hasNext();) {
               Relation relation = ri.nextRelation();
               addRelationToBackup(backupMap, relation);
               Node childNode = relation.getDestination();
               if (childNode.getNumber() == node.getNumber()) {
                  childNode = relation.getSource();
               }
               addNodeToBackup(backupMap, childNode, childDumpingNode);
            }
         }
      }
   }


   private void addRelationToBackup(HashMap<String, HashSet<Node>> backupMap, Relation relation) {
      String type = relation.getNodeManager().getName();
      HashSet<Node> set = backupMap.get(type);
      if (set == null) {
         set = new HashSet<Node>();
         backupMap.put(type, set);
      }
      set.add(relation);
   }
}
