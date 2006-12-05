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
package nl.leocms.pagina;

import javax.swing.tree.TreeModel;

import com.finalist.tree.TreeModelAdapter;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import nl.leocms.util.RubriekHelper;
import java.util.TreeMap;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Trivial implementation of a tree structure based on a cloud.
 * 
 * DO NOT OPTIMIZE THIS CODE unless it is measured to be slow!
 * ( premature optimization is the root of all evil - D. Knuth )
 * 
 * @author Gerard van de Weerd
 * Date :Oct 20 2003 
 * 
 */
public class PaginaTreeModel extends TreeModelAdapter implements TreeModel {

   private Cloud cloud;
   private static Logger log = Logging.getLoggerInstance(PaginaUtil.class);
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
      int t = 0;      
      // count rubrieken
      NodeList rubriekenList = n.getRelatedNodes("rubriek", "parent", "DESTINATION");
      t += rubriekenList.size();
      // count pagina's
      NodeList paginaList = n.getRelatedNodes("pagina", "posrel", "DESTINATION");      
      t += paginaList.size();            
      // return total      
      return t;
   }
 
   /* Counts the pages
    * 
    */
   public int getPageCount(Object parent) {
      Node n = (Node) parent;
      // count pagina's
      return n.getRelatedNodes("pagina", "posrel", "DESTINATION").size();      
   }
   
   
   /* Counts the subobjects
    * 
    */
   public int getSubObjectsCount(Object parent) {
      Node n = (Node) parent;
      // count pagina's and rubrieken
      return n.getRelatedNodes("pagina", "posrel", "DESTINATION").size()+ n.getRelatedNodes("rubriek", "parent", "DESTINATION").size();      
   }
   
 
   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
    */
   public boolean isLeaf(Object node) {
      Node n = (Node) node;
      if ("pagina".equals(n.getNodeManager().getName())) {
         return true;      
      }
      else {                  
         return getChildCount(n)==0;
      }
   }

   
   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
    */
   public Object getChild(Object parent, int index) {
      
      Node p = (Node) parent;
      RubriekHelper h = new RubriekHelper(cloud);
      TreeMap childs = (TreeMap) h.getSubObjects(""+p.getNumber(),true);
      int i=0;
      Integer thisKey = (Integer) childs.firstKey();
      while(i<index) {
         childs.remove(thisKey);
         thisKey = (Integer) childs.firstKey();
         i++;
      }
      return cloud.getNode((String) childs.get(thisKey));
   }

   /**
    * 
    */
   public PaginaTreeModel(Cloud c) {
      super();
      this.cloud = c;
   }

}
