package nl.didactor.metadata.tree;

import javax.swing.tree.TreeModel;
import nl.didactor.tree.TreeModelAdapter;

import org.mmbase.bridge.*;

import java.util.TreeMap;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/*
The metadata tree consists of:

   metastandard (-posrel-metastandard)* - posrel - metadefinition (- related - metavocabulary )*

*/
public class MetadataTreeModel extends TreeModelAdapter implements TreeModel {

   private Cloud cloud;
   private static Logger log = Logging.getLoggerInstance(MetadataTreeModel.class);

   public Object getRoot() {
      Node root = null;
      try {
         root = cloud.getNode("metadata_root");
      } catch (Exception e) {
         log.debug("No root found for metastandards, creating one now.");
         NodeManager nm = cloud.getNodeManager("metastandard");
         NodeList nl = nm.getList(null,"name","UP");
         root = nm.createNode();
         root.setStringValue("name","Metadata beheer");
         root.commit();
         root.createAlias("metadata_root");
         RelationManager rm = cloud.getRelationManager("posrel");
         for(int i=0; i<nl.size(); i++) {
            Relation rel = root.createRelation(nl.getNode(i),rm);
            rel.setIntValue("pos",i);
            rel.commit();
         }
      }
      return root;
   }

   public int getChildCount(Object parent) {
      // Note: querycache will improve performance when the same list statements in getChildCount and getChild are used
      Node p = (Node) parent;
      String sTypeDef = p.getNodeManager().getName();
      int iChildCount = 0;
      NodeList nl = null;
      log.debug("getChildCount(" + sTypeDef + " " + p.getStringValue((sTypeDef.equals("metavocabulary")?"value":"name")) + " (" + p.getNumber() + ")");
      if(sTypeDef.equals("metastandard")) {
         nl = cloud.getList(p.getStringValue("number"),"metastandard1,posrel,metastandard2","metastandard2.number", null, "posrel.pos", "UP", "DESTINATION", true);
         iChildCount += nl.size();
         nl = cloud.getList(p.getStringValue("number"),"metastandard,posrel,metadefinition","metadefinition.number", null, "posrel.pos", "UP", "DESTINATION", true);   
         iChildCount += nl.size();
      } else if(sTypeDef.equals("metadefinition")||sTypeDef.equals("metavocabulary")) {
         nl = cloud.getList(p.getStringValue("number"),sTypeDef + ",posrel,metavocabulary1","metavocabulary1.number", null, "posrel.pos", "UP", "DESTINATION", true);
         iChildCount += nl.size();
      } else {
         log.error("unsupported nodetype in getChildCount for " + sTypeDef + " " + p.getNumber());
      }
      log.debug("number of childs " + iChildCount);
      return iChildCount;
   }

   public boolean isLeaf(Object node) {
      Node n = (Node) node;
      return getChildCount(n)==0;
   }

   public Object getChild(Object parent, int index) {
      Node p = (Node) parent;
      String sTypeDef = p.getNodeManager().getName();
      Node childNode = null;
      NodeList nl = null;
      log.debug("getChild(" + sTypeDef 
            + " " + p.getStringValue( (sTypeDef.equals("metavocabulary")?"value":"name") ) 
            + " (" + p.getNumber() + ") at index " + index + ")");
      if(sTypeDef.equals("metastandard")) {
         nl = cloud.getList(p.getStringValue("number"),"metastandard,posrel,metastandard1","metastandard1.number", null, "posrel.pos", "UP", "DESTINATION", true);
         if(index<nl.size()) {
            childNode = cloud.getNode(nl.getNode(index).getStringValue("metastandard1.number"));
         } else {
            int iMetastandardCount = nl.size();
            nl = cloud.getList(p.getStringValue("number"),"metastandard,posrel,metadefinition","metadefinition.number", null, "posrel.pos", "UP", "DESTINATION", true);
            if(index < (nl.size() + iMetastandardCount)) {
               childNode = cloud.getNode(nl.getNode(index-iMetastandardCount).getStringValue("metadefinition.number"));
            }
         }
      } else if(sTypeDef.equals("metadefinition") || sTypeDef.equals("metavocabulary")) {
         nl = cloud.getList(p.getStringValue("number"),sTypeDef + ",posrel,metavocabulary1","metavocabulary1.number", null, "posrel.pos", "UP", "DESTINATION", true);
         if(index<nl.size()) {
            childNode = cloud.getNode(nl.getNode(index).getStringValue("metavocabulary1.number"));
         }
      }
      if(childNode==null) {
         log.error("in getChild less than " + index + " childs for " + sTypeDef + " " + p.getNumber());
      }
      log.debug("child is " + childNode.getStringValue((sTypeDef.equals("metavocabulary")?"value":"name")) + " (" + childNode.getNumber() + ")");
      return childNode;
   }

   public MetadataTreeModel(Cloud c) {
      super();
      this.cloud = c;
   }
}
