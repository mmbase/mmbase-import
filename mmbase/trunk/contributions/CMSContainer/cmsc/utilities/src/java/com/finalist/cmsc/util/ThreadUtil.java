/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.util.*;


public class ThreadUtil {

    private ThreadUtil() {
        // utility
    }

    public static Thread getThreadById(long id) {
        for (Thread thr : getAllThreads()) {
            if (thr.getId() == id) {
                return thr;
            }
        }
        return null;
    }
    
    public static Thread getThreadByName(String name) {
        for (Thread thr : getAllThreads()) {
            if (thr.getName().equals(name)) {
                return thr;
            }
        }
        return null;
    }
    
    public static Set<Thread> getAllThreads() {
        return Thread.getAllStackTraces().keySet();
    }
    
    public static ThreadGroup getRootThreadGroup() {
        // Find the root thread group
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }
    
    public static ThreadGroupTree createFullTree() {
        ThreadGroup root = getRootThreadGroup();
        // Visit each thread group
        return createTree(root, 0);
    }
    
    public static ThreadGroupTree createTree(ThreadGroup group, int level) {
        String name = group.getName();
        ThreadGroupTree tree = new ThreadGroupTree(name, level);
        
        // Get threads in `group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads*2];
        numThreads = group.enumerate(threads, false);
     
        // Enumerate each thread in `group'
        for (int i=0; i<numThreads; i++) {
            // Get thread
            Thread thread = threads[i];
            tree.addThread(thread);
        }
     
        // Get thread subgroups of `group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups*2];
        numGroups = group.enumerate(groups, false);
     
        // Recursively visit each subgroup
        for (int i=0; i<numGroups; i++) {
            ThreadGroupTree child = createTree(groups[i], level+1);
            tree.addGroup(child);
        }
        
        return tree;
    }
    
    public static class ThreadGroupTree {
        
        private String name;
        private int level;
        private List<ThreadGroupTree> groups = new ArrayList<ThreadGroupTree>();
        private List<Thread> threads = new ArrayList<Thread>();
        
        public ThreadGroupTree(String name, int level) {
            this.name = name;
            this.level = level;
        }
        
        public void addGroup(ThreadGroupTree group) {
            groups.add(group);
        }
        
        public void addThread(Thread thr) {
            threads.add(thr);
        }
        
        public String getName() {
            return name;
        }
        
        public int getLevel() {
            return level;
        }
        
        public List<ThreadGroupTree> getGroups() {
            return groups;
        }
        
        public List<Thread> getThreads() {
            return threads;
        }
    }
}
