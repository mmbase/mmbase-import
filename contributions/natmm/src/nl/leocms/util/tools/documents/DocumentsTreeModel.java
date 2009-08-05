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
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util.tools.documents;

import javax.swing.tree.TreeModel;

import com.finalist.tree.TreeModelAdapter;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Trivial implementation of a tree structure based on a cloud.
 *
 * DO NOT OPTIMIZE THIS CODE unless it is measured to be slow!
 * ( premature optimization is the root of all evil - D. Knuth )
 *
 * @author anna
 * Date :Oct 20 2005
 *
 */
public class DocumentsTreeModel extends TreeModelAdapter implements TreeModel {

   private Cloud cloud;
   private static Logger log = Logging.getLoggerInstance(DocumentsTreeModel.class);
   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getRoot()
    */
   public Object getRoot() {
      return cloud.getNode("documents_root");
   }

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
    */
   public int getChildCount(Object parent) {
      Node n = (Node) parent;
      // count documents's
      NodeList posrelDocumentsList = n.getRelatedNodes("documents", "posrel", "DESTINATION");
      return posrelDocumentsList.size();
   }

    /* Counts the subobjects
    *
    */
   public int getSubObjectsCount(Object parent) {
      Node n = (Node) parent;
      // count documents with posrel and parent relations
      return n.getRelatedNodes("documents", "posrel", "DESTINATION").size();
   }


   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
    */
   public boolean isLeaf(Object node) {
      Node n = (Node) node;
         return getChildCount(n)==0;
   }


   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
    */
   public Object getChild(Object parent, int index) {

      Node p = (Node) parent;

      Node childNode = null;
      NodeList nodeList = cloud.getList(""+p.getNumber(), "documents1,posrel,documents2", "documents2.number", null, "documents2.type,documents2.filename", "UP,UP", "DESTINATION", true);
      if ((nodeList != null) && (nodeList.size() > index)) {
         String childNodeNumber = nodeList.getNode(index).getStringValue("documents2.number");
         childNode = cloud.getNode(childNodeNumber);
      }
      return childNode;
   }

   /**
    *
    */
   public DocumentsTreeModel(Cloud c) {
      super();
      this.cloud = c;
   }

}
