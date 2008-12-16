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
public class RepositoryTreeModel implements TreeModel {
   private Cloud cloud;
   private boolean contentChannelOnly = false;


   public RepositoryTreeModel(Cloud c) {
      this.cloud = c;
   }


   public RepositoryTreeModel(Cloud c, boolean contentChannelOnly) {
      this.cloud = c;
      this.contentChannelOnly = contentChannelOnly;
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
      if (contentChannelOnly) {
         return RepositoryUtil.getContentChannelChildCount((Node) parent);
      }
      else {
         return RepositoryUtil.getChildCount((Node) parent);
      }
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
      Node parentNode = (Node) parent;
      NodeList channels;
      if (contentChannelOnly) {
         channels = RepositoryUtil.getContentChannelOrderedChildren(parentNode);
      }
      else {
         channels = RepositoryUtil.getOrderedChildren(parentNode);
      }

      if (channels.size() > index) {
         return channels.get(index);
      }
      else {
         throw new IndexOutOfBoundsException("Child " + index + "is not available. Node " + parentNode.getNumber()
               + " has " + channels.size() + "children.");
      }
   }


   public Object getNode(String id) {
      return cloud.getNode(id);
   }

}