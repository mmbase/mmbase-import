/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement.tree;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PageTree {

   private PageTreeNode root;
   private Map<Integer, PageTreeNode> treeNodes = new HashMap<Integer, PageTreeNode>();
   private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();


   public PageTree(int id, String fragment) {
      this.root = new PageTreeNode(fragment, id, 0);
      this.treeNodes.put(id, root);
   }


   public PageTreeNode getRoot() {
      return root;
   }


   public PageTreeNode remove(String path) {
      rwl.writeLock().lock();
      try {
         PageTreeNode oldNode = getPath(path);
         remove(oldNode);
         return oldNode;
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public PageTreeNode remove(String path, int destinationNumber) {
      rwl.writeLock().lock();
      try {
         PageTreeNode parentNode = getPath(path);
         if(parentNode != null) {
	         PageTreeNode oldNode = parentNode.getChildById(destinationNumber);
	         if (oldNode != null) {
	            remove(oldNode);
	         }
	         return oldNode;
         }
         return null;
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   private void remove(PageTreeNode oldNode) {
      rwl.writeLock().lock();
      try {
         oldNode.removeFromParent();
         treeNodes.remove(oldNode.getPage());
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public PageTreeNode insert(String pagePath, Integer page, int childIndex) {
      rwl.writeLock().lock();
      try {
         List<String> names = getPathElements(pagePath);
         if (names.size() > 1) {
            String pageName = names.remove(names.size() - 1);
            PageTreeNode newParemtNode = getPath(names);
            return insert(newParemtNode, page, pageName, childIndex);
         }
         else {
            // Page is a site
            throw new IllegalArgumentException("Page is a site");
         }
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public void replace(int sourceNumber, String pathfragement) {
      rwl.writeLock().lock();
      try {
         if (treeNodes.containsKey(sourceNumber)) {
            PageTreeNode rootTreeNode = treeNodes.get(sourceNumber);
            rootTreeNode.replace(pathfragement);
         }
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public PageTreeNode insert(int sourceNumber, Integer destNumber, String fragment, int childIndex) {
      rwl.writeLock().lock();
      try {
         PageTreeNode pageTreeNode = null;
         if (treeNodes.containsKey(sourceNumber)) {
            PageTreeNode rootTreeNode = treeNodes.get(sourceNumber);
            pageTreeNode = insert(rootTreeNode, destNumber, fragment, childIndex);
         }
         return pageTreeNode;
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   private PageTreeNode insert(PageTreeNode newParemtNode, Integer page, String pageName, int childIndex) {
      rwl.writeLock().lock();
      try {

         PageTreeNode newNode = new PageTreeNode(pageName, page, childIndex);
         newParemtNode.insert(newNode, childIndex);
         treeNodes.put(page, newNode);
         return newNode;
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public void move(PageTreeNode moveNode, int childIndex) {
      rwl.writeLock().lock();
      try {
         PageTreeNode newParemtNode = moveNode.getParent();
         newParemtNode.insert(moveNode, childIndex);
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public void move(PageTreeNode moveNode, int parentId, int childIndex) {
      rwl.writeLock().lock();
      try {
         PageTreeNode newParemtNode = getPageTreeNode(parentId);
         newParemtNode.insert(moveNode, childIndex);
      }
      finally {
         rwl.writeLock().unlock();
      }
   }


   public void move(PageTreeNode moveNode, PageTree newTree, int parentId, int childIndex) {
      rwl.writeLock().lock();
      newTree.rwl.writeLock().lock();
      try {
         PageTreeNode newParemtNode = newTree.getPageTreeNode(parentId);
         newParemtNode.insert(moveNode, childIndex);
         moveToNewTree(moveNode, newTree);
      }
      finally {
         rwl.writeLock().unlock();
         newTree.rwl.writeLock().unlock();
      }
   }


   private void moveToNewTree(PageTreeNode moveNode, PageTree newTree) {
      treeNodes.remove(moveNode.getPage());
      newTree.treeNodes.put(moveNode.getPage(), moveNode);

      List<PageTreeNode> children = moveNode.getChildren();
      for (PageTreeNode node : children) {
         moveToNewTree(node, newTree);
      }
   }


   /**
    * Get the PageTreeNode with the specified path.
    * 
    * @param path
    *           Path of PageTreeNode to be retrieved.
    * @return Returns the PageTreeNode with the specified path. If this path is
    *         invalid this method will return <code>null</code>.
    */
   public PageTreeNode getPath(String path) {
      rwl.readLock().lock();
      try {
         List<String> names = getPathElements(path);
         return getPath(names);
      }
      finally {
         rwl.readLock().unlock();
      }
   }


   /**
    * Get the PageTreeNode with the specified path.
    * 
    * @param names
    *           Path of PageTreeNode to be retrieved.
    * @return Returns the PageTreeNode with the specified path. If this path is
    *         invalid this method will return <code>null</code>.
    */
   public PageTreeNode getPath(List<String> names) {
      rwl.readLock().lock();
      try {
         return root.getPath(names);
      }
      finally {
         rwl.readLock().unlock();
      }
   }


   public boolean containsPageTreeNode(int id) {
      rwl.readLock().lock();
      try {
         return treeNodes.containsKey(id);
      }
      finally {
         rwl.readLock().unlock();
      }

   }


   public PageTreeNode getPageTreeNode(int id) {
      rwl.readLock().lock();
      try {
         return treeNodes.get(id);
      }
      finally {
         rwl.readLock().unlock();
      }
   }


   public void addPages(List<String> names, List<Integer> pageIds) {
      rwl.readLock().lock();
      try {
         root.addPages(names, pageIds);
      }
      finally {
         rwl.readLock().unlock();
      }
   }


   /**
    * Convert a path into a list of the names.
    * 
    * @param path
    *           String containing the full path with forward slash ('/')
    *           seprating the path element names.
    * @return List of String objects containing the names starting with the root
    *         element and working to the end.
    */
   public static List<String> getPathElements(String path) {
      path = path.toLowerCase();
      if (path.startsWith(PageTreeNode.SC)) {
         path = path.substring(1);
      }
      List<String> elements = new ArrayList<String>();
      StringTokenizer st = new StringTokenizer(path, PageTreeNode.SC);
      while (st.hasMoreTokens()) {
         elements.add(st.nextToken());
      }
      return elements;
   }

}
