package com.finalist.cmsc.repository;

import org.mmbase.bridge.*;

import com.finalist.tree.TreeModel;

/**
 * Trivial implementation of a tree structure based on a cloud. DO NOT OPTIMIZE
 * THIS CODE unless it is measured to be slow! ( premature optimization is the
 * root of all evil - D. Knuth )
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public class RepositoryTrashTreeModel implements TreeModel {
   private Cloud cloud;


   public RepositoryTrashTreeModel(Cloud c) {
      this.cloud = c;
   }


   public RepositoryTrashTreeModel(Cloud c, boolean contentChannelOnly) {
      this.cloud = c;
   }


   /**
    * @see javax.swing.tree.TreeModel#getRoot()
    */
   public Object getRoot() {
      return RepositoryUtil.getTrashNode(cloud);
   }


   /**
    * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
    */
   public int getChildCount(Object parent) {
      // no childs
      return 0;
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
      // no childs
      return null;
   }


   public Object getNode(String id) {
      return cloud.getNode(id);
   }
}