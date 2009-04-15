/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree;

public interface TreeModel {

   /**
    * Returns the root of the tree. Returns <code>null</code> only if the tree
    * has no nodes.
    * 
    * @return the root of the tree
    */
   public Object getRoot();


   /**
    * Returns the child of <code>parent</code> at index <code>index</code>
    * in the parent's child array. <code>parent</code> must be a node
    * previously obtained from this data source. This should not return
    * <code>null</code> if <code>index</code> is a valid index for
    * <code>parent</code> (that is <code>index >= 0 &&
    * index < getChildCount(parent</code>)).
    * 
    * @param parent
    *           a node in the tree, obtained from this data source
    * @param index
    *           in the child array
    * @return the child of <code>parent</code> at index <code>index</code>
    */
   public Object getChild(Object parent, int index);


   /**
    * Returns the number of children of <code>parent</code>. Returns 0 if the
    * node is a leaf or if it has no children. <code>parent</code> must be a
    * node previously obtained from this data source.
    * 
    * @param parent
    *           a node in the tree, obtained from this data source
    * @return the number of children of the node <code>parent</code>
    */
   public int getChildCount(Object parent);


   /**
    * Returns <code>true</code> if <code>node</code> is a leaf. It is
    * possible for this method to return <code>false</code> even if
    * <code>node</code> has no children. A directory in a filesystem, for
    * example, may contain no files; the node representing the directory is not
    * a leaf, but it also has no children.
    * 
    * @param node
    *           a node in the tree, obtained from this data source
    * @return true if <code>node</code> is a leaf
    */
   public boolean isLeaf(Object node);


   /**
    * Returns a node in the tree based on the id.
    * 
    * @param id
    *           identification string of a node in this data source
    * @return node in the tree
    */
   public Object getNode(String id);
}
