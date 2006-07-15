/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl.services.sitemanagement.tree;

import java.util.*;

public class PageTree {

    private PageTreeNode root;
    private Map<Integer, PageTreeNode> treeNodes = new HashMap<Integer, PageTreeNode>();
    
    public PageTree(int id, String fragment) {
        this.root = new PageTreeNode(fragment, id, 0);
        this.treeNodes.put(id, root);
    }
    
    public PageTreeNode getRoot() {
        return root;
    }
    
    public void replace(String oldpath,  String newpath, Integer page, int childIndex) {
        PageTreeNode oldNode = getPath(oldpath);
        if (newpath.equals(oldpath)) {
            // Page is still on the same position
            oldNode.replace(page, childIndex);
        }
        else {
            List<String> names = getPathElements(newpath);
            if (names.size() > 1) {
                String pageName = names.remove(names.size() - 1);
                PageTreeNode newParemtNode = getPath(names);
                if (newParemtNode.equals(oldNode.getParent())) {
                    // Page is renamed
                    oldNode.replace(pageName, page, childIndex);
                }
                else {
                    // Page is moved to another location
                    newParemtNode.insert(oldNode, childIndex);
                }
            }
            else {
                // Page is now a site
                throw new IllegalArgumentException("Page is a site, call remove method");
            }
        }
    }
    
    public PageTreeNode remove(String path) {
        PageTreeNode oldNode = getPath(path);
        remove(oldNode);
        return oldNode;
    }

    public PageTreeNode remove(String path, int destinationNumber) {
        PageTreeNode parentNode = getPath(path);
        PageTreeNode oldNode = parentNode.getChildById(destinationNumber);
        remove(oldNode);
        return oldNode;
    }

    private void remove(PageTreeNode oldNode) {
        oldNode.removeFromParent();
        treeNodes.remove(oldNode.getPage());
    }
    
    public PageTreeNode insert(String pagePath, Integer page, int childIndex) {
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


    public void replace(int sourceNumber, String pathfragement) {
        if (treeNodes.containsKey(sourceNumber)) {
            PageTreeNode rootTreeNode = treeNodes.get(sourceNumber);
            rootTreeNode.replace(pathfragement);
        }
    }
    
    public PageTreeNode insert(int sourceNumber, Integer destNumber, String fragment, int childIndex) {
        PageTreeNode pageTreeNode = null;
        if (treeNodes.containsKey(sourceNumber)) {
            PageTreeNode rootTreeNode = treeNodes.get(sourceNumber);
            pageTreeNode = insert(rootTreeNode, destNumber, fragment, childIndex);
        }
        return pageTreeNode;
    }

    private PageTreeNode insert(PageTreeNode newParemtNode, Integer page, String pageName, int childIndex) {
        PageTreeNode newNode = new PageTreeNode(pageName, page, childIndex);
        newParemtNode.insert(newNode, childIndex);
        treeNodes.put(page, newNode);
        return newNode;
    }

    /**
     * Get the PageTreeNode with the specified path.
     *
     * @param path Path of PageTreeNode to be retrieved.
     *
     * @return Returns the PageTreeNode with the specified path. If this
     *     path is invalid this method will return <code>null</code>.
     */
    public PageTreeNode getPath(String path) {
       List<String> names = getPathElements(path);
       return getPath(names);
    }

    /**
     * Get the PageTreeNode with the specified path.
     *
     * @param names Path of PageTreeNode to be retrieved.
     * @return Returns the PageTreeNode with the specified path. If this
     *     path is invalid this method will return <code>null</code>.
     */
    public PageTreeNode getPath(List<String> names) {
        return root.getPath(names);
    }

    public boolean containsPageTreeNode(int id) {
        return treeNodes.containsKey(id);
    }

    public PageTreeNode getPageTreeNode(int id) {
        return treeNodes.get(id);
    }

    public void addPages(List<String> names, List<Integer> pageIds) {
        root.addPages(names, pageIds);
    }
    
    /**
     * Convert a path into a list of the names.
     *
     * @param path String containing the full path with
     *     forward slash ('/') seprating the path element names.
     *
     * @return List of String objects containing the names starting
     *     with the root element and working to the end.
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
