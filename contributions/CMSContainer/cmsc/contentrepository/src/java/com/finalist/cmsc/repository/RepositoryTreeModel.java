package com.finalist.cmsc.repository;

import org.mmbase.bridge.*;

import com.finalist.tree.TreeModel;

/**
 * Trivial implementation of a tree structure based on a cloud.
 * 
 * DO NOT OPTIMIZE THIS CODE unless it is measured to be slow!
 * ( premature optimization is the root of all evil - D. Knuth )
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public class RepositoryTreeModel implements TreeModel {
   private Cloud cloud;

   public RepositoryTreeModel(Cloud c) {
      this.cloud = c;
   }
   
   /**
    * @see javax.swing.tree.TreeModel#getRoot()
    */
   public Object getRoot() {
      return RepositoryUtil.getRootNode(cloud);
   }

   /**
    * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
    */
   public int getChildCount(Object parent) {
      return RepositoryUtil.getChildCount((Node) parent);
   }
 
   /**
    * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
    */
   public boolean isLeaf(Object node) {
      return getChildCount(node) == 0;
   }

   /**
    * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
    */
   public Object getChild(Object parent, int index) {
      Node parentNode = (Node)parent;
      NodeList contentChannels = RepositoryUtil.getOrderedChildren(parentNode); 
      
      if (contentChannels.size() > index) {
         return contentChannels.get(index);
      }
      else {
          throw new IndexOutOfBoundsException("Child " + index + "is not available. Node " + parentNode.getNumber() + " has " + contentChannels.size() + "children.");
      }
   }

    public Object getNode(String id) {
        return cloud.getNode(id);
    }

}