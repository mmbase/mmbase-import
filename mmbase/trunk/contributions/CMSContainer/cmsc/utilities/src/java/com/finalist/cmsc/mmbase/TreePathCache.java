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

    public static Integer getChannelFromCache(String nodeManagerName, String path) {
        return getChannelFromCache(getCache(nodeManagerName), path);
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
    
   
    private static String getPathStringFromCache(BidiMap channelCache, int node) {
       synchronized(channelCache) {
          TreePathCacheKey key = (TreePathCacheKey) channelCache.getKey(new Integer(node));
          if (key != null) {
             return key.getPath();
          }
          return null;
       }
    }

    private static Integer getChannelFromCache(BidiMap channelCache, String path) {
       synchronized(channelCache) {
          return (Integer) channelCache.get(new TreePathCacheKey(path));
       }
    }

    private static void addToCache(BidiMap channelCache, String path, int node) {
       synchronized(channelCache) {
          channelCache.put(new TreePathCacheKey(path), new Integer(node));
       }
    }

    private static void removeFromCache(BidiMap channelCache, int node) {
       synchronized(channelCache) {
          channelCache.removeValue(new Integer(node));
       }
    }

    private static void clearCache(BidiMap channelCache) {
       synchronized(channelCache) {
          channelCache.clear();
       }
    }

    private static void updateCache(BidiMap channelCache, int node, String name) {
       synchronized(channelCache) {
          Integer nodeNumber = new Integer(node);
          if (channelCache.containsValue(nodeNumber)) {
             TreePathCacheKey cKey = (TreePathCacheKey) channelCache.getKey(nodeNumber);
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
             replaceCache(channelCache, nodeNumber, newPath);
          }
       }
    }

    private static void replaceCache(BidiMap channelCache, Integer nodeNumber, String newPath) {
        synchronized (channelCache) {
            TreePathCacheKey cKey = (TreePathCacheKey) channelCache.removeValue(nodeNumber);;
            channelCache.put(new TreePathCacheKey(newPath), nodeNumber);

            String path = cKey.getPath();
            List<TreePathCacheKey> entriesToChange = new ArrayList<TreePathCacheKey>();

            Iterator iter = channelCache.keySet().iterator();
            while (iter.hasNext()) {
                TreePathCacheKey key = (TreePathCacheKey) iter.next();
                if (key.getPath().startsWith(path + '/')) {
                    entriesToChange.add(key);
                }
            }

            Iterator entriesIter = entriesToChange.iterator();
            while (entriesIter.hasNext()) {
                TreePathCacheKey key = (TreePathCacheKey) entriesIter.next();
                Integer tempNode = (Integer) channelCache.remove(key);
                String newKey = key.getPath().replaceFirst(path + '/', newPath + '/');
                channelCache.put(new TreePathCacheKey(newKey), tempNode);
            }
        }
    }
    
}
