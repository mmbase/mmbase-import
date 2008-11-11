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
package nl.leocms.rubrieken;

import javax.swing.tree.TreeModel;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import com.finalist.tree.*;
/**
 * Trivial implementation of a tree structure based on a cloud.
 * 
 * DO NOT OPTIMIZE THIS CODE unless it is measured to be slow!
 * ( premature optimization is the root of all evil - D. Knuth )
 * 
 * @author Edwin van der Elst
 * Date :Sep 15, 2003
 * 
 */
public class RubriekTreeModel extends TreeModelAdapter implements TreeModel {

   private Cloud cloud;

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getRoot()
    */
   public Object getRoot() {
      return cloud.getNode("root");
   }

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
    */
   public int getChildCount(Object parent) {
      Node n = (Node) parent; 
      NodeList list = n.getRelatedNodes("rubriek", "parent", "DESTINATION");
      int s = list.size();
      return s;
   }
 
   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
    */
   public boolean isLeaf(Object node) {
      Node n = (Node) node;
      NodeList list = n.getRelatedNodes("rubriek", "parent", "DESTINATION");
      int s = list.size();
      return s==0;
   }

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
    */
   public Object getChild(Object parent, int index) {
      Node n = (Node)parent;
      Node p = (Node)parent;
      NodeList sl = cloud.getList( ""+p.getNumber(), "rubriek,childrel,rubriek2","rubriek2.number",null,"childrel.pos","UP","DESTINATION",true);
      return cloud.getNode( sl.getNode(index).getIntValue("rubriek2.number"));
   }

   /**
    * 
    */
   public RubriekTreeModel(Cloud c) {
      super();
      this.cloud = c;
   }

}
