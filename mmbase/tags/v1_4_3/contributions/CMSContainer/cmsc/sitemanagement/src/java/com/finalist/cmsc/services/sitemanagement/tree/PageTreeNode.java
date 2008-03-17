/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement.tree;

import java.util.*;

public class PageTreeNode {

   /** Tree separator character */
   public final static String SC = "/";

   private PageTreeNode parent;
   private Vector<PageTreeNode> children = new Vector<PageTreeNode>();
   private String pathfragement;
   private Integer page;
   private int pos;


   public PageTreeNode(String pathfragement, Integer page, int pos) {
      this.pathfragement = pathfragement;
      this.page = page;
      this.pos = pos;
   }


   public PageTreeNode(PageTreeNode parent, String pathfragement, Integer page, int pos) {
      this.parent = parent;
      this.pathfragement = pathfragement;
      this.page = page;
      this.pos = pos;
   }


   /**
    * Getter for parent in Tree.
    * 
    * @return Parent or null if no parent was found.
    */
   public PageTreeNode getParent() {
      return parent;
   }


   /**
    * Sets this node's parent to <code>newParent</code> but does not change
    * the parent's child array. This method is called from <code>insert()</code>
    * and <code>remove()</code> to reassign a child's parent,
    * 
    * @param newParent
    *           this node's new parent
    */
   void setParent(PageTreeNode newParent) {
      parent = newParent;
   }


   void setPos(int pos) {
      this.pos = pos;
   }


   /**
    * Get children.
    * 
    * @return List of elements that are children of this element.
    */
   public List<PageTreeNode> getChildren() {
      if (children == null) {
         children = new Vector<PageTreeNode>();
      }
      return Collections.unmodifiableList(children);
   }


   /**
    * Get the number of child elements.
    * 
    * @return Numder of child navigation elements.
    */
   public int getChildCount() {
      return (children == null) ? 0 : children.size();
   }


   /**
    * Get the depth of the element in relation to the root element. The root
    * element has a depth of 0.
    * 
    * @return Returns the element depth.
    */
   public int getDepth() {
      if (parent != null) {
         return parent.getDepth() + 1;
      }
      return 0;
   }


   /**
    * Get the element path
    * 
    * @return Element path.
    */
   public String getPathStr() {
      return getPathStr(true);
   }


   /**
    * Get the element path
    * 
    * @param includeRoot -
    *           include the root pathfragment
    * @return Element path.
    */
   public String getPathStr(boolean includeRoot) {
      if (parent != null) {
         String parentPathStr = parent.getPathStr(includeRoot);
         if (parentPathStr.length() > 0) {
            return parentPathStr + SC + pathfragement;
         }
         else {
            return pathfragement;
         }
      }
      if (includeRoot) {
         return pathfragement;
      }
      return "";
   }


   /**
    * Removes <code>newChild</code> from its present parent (if it has a
    * parent), sets the child's parent to this node, and then adds the child to
    * this node's children at index <code>childIndex</code>.
    * <code>newChild</code> must not be null and must not be an ancestor of
    * this node.
    * 
    * @param newChild
    *           the MutableTreeNode to insert under this node
    * @param childIndex
    *           the index in this node's child array where this node is to be
    *           inserted
    * @exception IllegalArgumentException
    *               if <code>newChild</code> is null or is an ancestor of this
    *               node
    */
   void insert(PageTreeNode newChild, int childIndex) {
      if (newChild == null) {
         throw new IllegalArgumentException("new child is null");
      }
      else if (isNodeAncestor(newChild)) {
         throw new IllegalArgumentException("new child is an ancestor");
      }

      PageTreeNode oldParent = newChild.getParent();

      if (oldParent != null) {
         oldParent.remove(newChild);
      }
      newChild.setParent(this);
      newChild.setPos(childIndex);

      if (children == null) {
         children = new Vector<PageTreeNode>();
      }
      else {
         if (children.isEmpty()) {
            children.add(newChild);
         }
         else {
            int childrenSize = children.size();
            for (int i = 0; i < childrenSize; i++) {
               PageTreeNode child = children.get(i);
               if (childIndex < child.pos) {
                  children.insertElementAt(newChild, i);
                  break;
               }
               else {
                  if (i == childrenSize - 1) {
                     children.add(newChild);
                     break;
                  }
                  else {
                     continue;
                  }
               }
            }
         }
      }
   }


   /**
    * Removes <code>aChild</code> from this node's child list, giving it a
    * null parent.
    * 
    * @param aChild
    *           a child of this node to remove
    * @exception IllegalArgumentException
    *               if <code>aChild</code> is null or is not a child of this
    *               node
    */
   void remove(PageTreeNode aChild) {
      if (aChild == null) {
         throw new IllegalArgumentException("argument is null");
      }
      if (!isNodeChild(aChild)) {
         throw new IllegalArgumentException("argument is not a child");
      }
      remove(getIndex(aChild)); // linear search
   }


   /**
    * Removes the child at the specified index from this node's children and
    * sets that node's parent to null. The child node to remove must be a
    * <code>MutableTreeNode</code>.
    * 
    * @param childIndex
    *           the index in this node's child array of the child to remove
    * @exception ArrayIndexOutOfBoundsException
    *               if <code>childIndex</code> is out of bounds
    */
   void remove(int childIndex) {
      PageTreeNode child = getChildAt(childIndex);
      children.removeElementAt(childIndex);
      child.setParent(null);
   }


   /**
    * Returns the index of the specified child in this node's child array. If
    * the specified node is not a child of this node, returns <code>-1</code>.
    * This method performs a linear search and is O(n) where n is the number of
    * children.
    * 
    * @param aChild
    *           the TreeNode to search for among this node's children
    * @exception IllegalArgumentException
    *               if <code>aChild</code> is null
    * @return an int giving the index of the node in this node's child array, or
    *         <code>-1</code> if the specified node is a not a child of this
    *         node
    */
   public int getIndex(PageTreeNode aChild) {
      if (aChild == null) {
         throw new IllegalArgumentException("argument is null");
      }

      if (!isNodeChild(aChild)) {
         return -1;
      }
      return children.indexOf(aChild); // linear search
   }


   /**
    * Returns true if <code>aNode</code> is a child of this node. If
    * <code>aNode</code> is null, this method returns false.
    * 
    * @param aNode
    *           node to test as a child of this node
    * @return true if <code>aNode</code> is a child of this node; false if
    *         <code>aNode</code> is null
    */
   public boolean isNodeChild(PageTreeNode aNode) {
      boolean retval;

      if (aNode == null) {
         retval = false;
      }
      else {
         if (getChildCount() == 0) {
            retval = false;
         }
         else {
            retval = (aNode.getParent() == this);
         }
      }

      return retval;
   }


   /**
    * Returns true if <code>anotherNode</code> is an ancestor of this node --
    * if it is this node, this node's parent, or an ancestor of this node's
    * parent. (Note that a node is considered an ancestor of itself.) If
    * <code>anotherNode</code> is null, this method returns false.
    * 
    * @param anotherNode
    *           node to test as an ancestor of this node
    * @return true if this node is a descendant of <code>anotherNode</code>
    */
   public boolean isNodeAncestor(PageTreeNode anotherNode) {
      if (anotherNode == null) {
         return false;
      }

      PageTreeNode ancestor = this;

      do {
         if (ancestor == anotherNode) {
            return true;
         }
      } while ((ancestor = ancestor.getParent()) != null);

      return false;
   }


   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((pathfragement == null) ? 0 : pathfragement.hashCode());
      return result;
   }


   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final PageTreeNode other = (PageTreeNode) obj;
      if (pathfragement == null) {
         if (other.pathfragement != null)
            return false;
      }
      else if (!pathfragement.equals(other.pathfragement))
         return false;
      return true;
   }


   void removeFromParent() {
      PageTreeNode parent = getParent();
      if (parent != null) {
         parent.remove(this);
      }
   }


   public PageTreeNode getChildAt(int index) {
      if (children == null) {
         throw new ArrayIndexOutOfBoundsException("node has no children");
      }
      return children.elementAt(index);
   }


   public boolean isLeaf() {
      return (getChildCount() == 0);
   }


   /**
    * Returns this node's page.
    * 
    * @return the page stored at this node
    */
   public Integer getPage() {
      return page;
   }


   /**
    * Returns this node's path fragment.
    * 
    * @return the path fragment stored at this node
    */
   public String getPathfragement() {
      return pathfragement;
   }


   /**
    * Sets the state for this node.
    * 
    * @param pathfragement
    *           fragment of path
    */
   void replace(String pathfragement) {
      this.pathfragement = pathfragement;
   }


   /**
    * Move the <code>page</code> tp another position.
    * 
    * @param aChild
    *           the child which should be moved
    * @param childIndex
    *           index of this page in the parent
    */
   public void move(PageTreeNode aChild, int childIndex) {
      int oldIndex = getIndex(aChild);
      if (oldIndex != childIndex) {
         children.removeElementAt(oldIndex);

         int childrenSize = children.size();
         for (int i = 0; i < childrenSize; i++) {
            PageTreeNode child = children.get(i);
            if (childIndex < child.pos) {
               children.insertElementAt(aChild, i);
               break;
            }
            else {
               if (i == childrenSize - 1) {
                  children.add(aChild);
                  break;
               }
               else {
                  continue;
               }
            }
         }
      }
   }


   public PageTreeNode getPath(List<String> names) {
      String name = names.get(0);
      if (name.equalsIgnoreCase(pathfragement)) {
         if (names.size() == 1) {
            return this;
         }
         else {
            if (children != null) {
               PageTreeNode node = null;
               names.remove(0);
               for (int c = 0; c < children.size(); c++) {
                  PageTreeNode child = children.get(c);
                  node = child.getPath(names);
                  if (node != null) {
                     break;
                  }
               }
               names.add(0, pathfragement);
               return node;
            }
         }
      }
      return null;
   }


   public boolean addPages(List<String> names, List<Integer> pageIds) {
      String name = names.get(0);
      if (name.equalsIgnoreCase(pathfragement)) {
         pageIds.add(this.getPage());
         if (names.size() == 1) {
            return true;
         }
         else {
            if (children != null) {
               names.remove(0);
               for (int c = 0; c < children.size(); c++) {
                  PageTreeNode child = children.get(c);
                  if (child.addPages(names, pageIds)) {
                     break;
                  }
               }
               names.add(0, pathfragement);
               return true;
            }
         }
      }
      return false;
   }


   public PageTreeNode getChildById(int destinationNumber) {
      for (int c = 0; c < children.size(); c++) {
         PageTreeNode child = children.get(c);
         if (child.getPage() == destinationNumber) {
            return child;
         }
      }
      return null;
   }

}
