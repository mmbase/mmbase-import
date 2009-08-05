package com.finalist.cmsc.navigation;

import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.tree.TreeModel;

/**
 * Trivial implementation of a tree structure based on a cloud.
 * 
 * DO NOT OPTIMIZE THIS CODE unless it is measured to be slow!
 * ( premature optimization is the root of all evil - D. Knuth )
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public class NavigationTreeModel implements TreeModel {
   private Node site;
   private boolean strictPageOnly = false;

   public NavigationTreeModel(Node site) {
      this.site = site;
   }
   
   public NavigationTreeModel(Node site, boolean strictPageOnly) {
      this.site = site;
      this.strictPageOnly = strictPageOnly;
   }
   
   /**
    * @see com.finalist.tree.TreeModel#getRoot()
    */
   public Object getRoot() {
      return site;
   }

   /**
    * @see com.finalist.tree.TreeModel#getChildCount(java.lang.Object)
    */
   public int getChildCount(Object parent) {
       if (strictPageOnly) {
          return NavigationUtil.getStrictPageChildCount((Node) parent);
       }
       else {
          return NavigationUtil.getChildCount((Node) parent);
       }
   }
 
   /**
    * @see com.finalist.tree.TreeModel#isLeaf(java.lang.Object)
    */
   public boolean isLeaf(Object node) {
      return getChildCount(node) == 0;
   }

   /**
    * @see com.finalist.tree.TreeModel#getChild(java.lang.Object, int)
    */
   public Object getChild(Object parent, int index) {
      Node parentNode = (Node)parent;
      NodeList pages = NavigationUtil.getOrderedChildren(parentNode); 
      if (strictPageOnly) {
         pages = NavigationUtil.getStrictPageOrderedChildren(parentNode);
      }
      else {
         pages = NavigationUtil.getOrderedChildren(parentNode); 
      }

      if (pages.size() > index) {
         return pages.get(index);
      }
      throw new IndexOutOfBoundsException("Child " + index + " is not available. Node " + parentNode.getNumber() + " has " + pages.size() + " children.");
   }

    public Object getNode(String id) {
        return site.getCloud().getNode(id);
    }

}
