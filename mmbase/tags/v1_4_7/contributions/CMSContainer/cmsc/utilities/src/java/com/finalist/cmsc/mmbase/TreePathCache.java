/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.mmbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

public class TreePathCache {

    private static Map<String,BidiMap> treeCaches = new HashMap<String,BidiMap>();
    
    private TreePathCache() {
        //utility
    }

    public static void addToCache(String nodeManagerName, String pathStr, int number) {
        addToCache(getCache(nodeManagerName), pathStr, number);
    }

    public static String getPathStringFromCache(String nodeManagerName, int number) {
        return getPathStringFromCache(getCache(nodeManagerName), number);
    }

    public static Integer getTreeItemFromCache(String nodeManagerName, String path) {
        return getTreeItemFromCache(getCache(nodeManagerName), path);
    }

    public static void removeFromCache(String nodeManagerName, int node) {
        removeFromCache(getCache(nodeManagerName), node);
    }

    public static void clearCache(String nodeManagerName) {
        clearCache(getCache(nodeManagerName));
    }
    public static void updateCache(String nodeManagerName, int node, String name) {
        updateCache(getCache(nodeManagerName), node, name);
    }
    public static void moveCache(String nodeManagerName, int node, String newPath) {
        replaceCache(getCache(nodeManagerName), node, newPath);
    }
    
    private static BidiMap getCache(String nodeManagerName) {
        synchronized (treeCaches) {
            if (treeCaches.containsKey(nodeManagerName)) {
                return treeCaches.get(nodeManagerName);
            }
            else {
                BidiMap treeCache = new DualHashBidiMap();
                treeCaches.put(nodeManagerName, treeCache);
                return treeCache;
            }
        }
    }
    
   
    private static String getPathStringFromCache(BidiMap treeCache, int node) {
       synchronized(treeCache) {
          TreePathCacheKey key = (TreePathCacheKey) treeCache.getKey(new Integer(node));
          if (key != null) {
             return key.getPath();
          }
          return null;
       }
    }

    private static Integer getTreeItemFromCache(BidiMap treeCache, String path) {
       synchronized(treeCache) {
          return (Integer) treeCache.get(new TreePathCacheKey(path));
       }
    }

    private static void addToCache(BidiMap treeCache, String path, int node) {
       synchronized(treeCache) {
          treeCache.put(new TreePathCacheKey(path), new Integer(node));
       }
    }

    private static void removeFromCache(BidiMap treeCache, int node) {
       synchronized(treeCache) {
          treeCache.removeValue(new Integer(node));
       }
    }

    private static void clearCache(BidiMap treeCache) {
       synchronized(treeCache) {
          treeCache.clear();
       }
    }

    private static void updateCache(BidiMap treeCache, int node, String name) {
       synchronized(treeCache) {
          Integer nodeNumber = new Integer(node);
          if (treeCache.containsValue(nodeNumber)) {
             TreePathCacheKey cKey = (TreePathCacheKey) treeCache.getKey(nodeNumber);
             String path = cKey.getPath();
             String newPath = "";
             int index = path.lastIndexOf(TreeUtil.PATH_SEPARATOR);
             if (index > -1) {
                newPath = path.substring(0, index)  + TreeUtil.PATH_SEPARATOR + name;
             }
             else {
                // root node changed
                newPath = name;
             }
             replaceCache(treeCache, nodeNumber, newPath);
          }
       }
    }

    private static void replaceCache(BidiMap treeCache, Integer nodeNumber, String newPath) {
        synchronized (treeCache) {
            TreePathCacheKey cKey = (TreePathCacheKey) treeCache.removeValue(nodeNumber);;
            treeCache.put(new TreePathCacheKey(newPath), nodeNumber);

            String path = cKey.getPath();
            List<TreePathCacheKey> entriesToChange = new ArrayList<TreePathCacheKey>();

            Iterator<TreePathCacheKey> iter = treeCache.keySet().iterator();
            while (iter.hasNext()) {
                TreePathCacheKey key = iter.next();
                if (key.getPath().startsWith(path + '/')) {
                    entriesToChange.add(key);
                }
            }

            Iterator<TreePathCacheKey> entriesIter = entriesToChange.iterator();
            while (entriesIter.hasNext()) {
                TreePathCacheKey key = entriesIter.next();
                Integer tempNode = (Integer) treeCache.remove(key);
                String newKey = key.getPath().replaceFirst(path + '/', newPath + '/');
                treeCache.put(new TreePathCacheKey(newKey), tempNode);
            }
        }
    }
    
}
