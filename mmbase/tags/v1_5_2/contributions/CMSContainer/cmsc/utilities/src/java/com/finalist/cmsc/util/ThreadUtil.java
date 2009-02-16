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


   public static Map<Thread, StackTraceElement[]> getActiveThreads() {
      Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
      filterActiveThread(all);
      return all;
   }


   public static void filterActiveThread(Map<Thread, StackTraceElement[]> all) {
      for (Iterator<StackTraceElement[]> iterator = all.values().iterator(); iterator.hasNext();) {
         StackTraceElement[] stack = iterator.next();
         if (isWaiting(stack)) {
            iterator.remove();
         }
      }
   }


   public static Map<String, Map<Thread, StackTraceElement[]>> getThreadsByApplication() {
      Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
      return sortByApplication(all);
   }


   public static Map<String, Map<Thread, StackTraceElement[]>> getActiveThreadsByApplication() {
      Map<Thread, StackTraceElement[]> all = getActiveThreads();
      return sortByApplication(all);
   }


   public static Map<String, Map<Thread, StackTraceElement[]>> sortByApplication(Map<Thread, StackTraceElement[]> all) {
      Map<String, Map<Thread, StackTraceElement[]>> applications = new HashMap<String, Map<Thread, StackTraceElement[]>>();

      for (Map.Entry<Thread, StackTraceElement[]> entry : all.entrySet()) {
         String application = "Application";
         String threadname = entry.getKey().getName();
         if (threadname.startsWith("VM ") || threadname.startsWith("JDWP ") || threadname.startsWith("CompilerThread")
               || threadname.startsWith("Timer") || threadname.startsWith("GC ")
               || threadname.equals("Reference Handler") || threadname.equals("Finalizer")
               || threadname.equals("Signal Dispatcher") || threadname.equals("Low Memory Detector")) {
            application = "Java";
         }
         if (threadname.startsWith("RMI ")) {
            application = "RMI";
         }
         if (threadname.startsWith("MMBase") || threadname.startsWith("ImageConvert")
               || threadname.startsWith("ModuleProbe") || threadname.startsWith("MMServers")
               || threadname.startsWith("JOBTHREAD") || threadname.startsWith("emailexpireprobe")) {
            application = "MMBase";
         }
         if (threadname.startsWith("ContainerBackgroundProcessor")) {
            application = "Tomcat";
         }
         if (threadname.startsWith("TP-") || threadname.startsWith("http-")) {
            application = "TomcatRequests";
         }
         Map<Thread, StackTraceElement[]> applicationThreads = applications.get(application);
         if (applicationThreads == null) {
            applicationThreads = new HashMap<Thread, StackTraceElement[]>();
            applications.put(application, applicationThreads);
         }
         applicationThreads.put(entry.getKey(), entry.getValue());
      }

      return applications;
   }


   public static boolean isWaiting(StackTraceElement[] stack) {
      if (stack != null && stack.length > 0) {
         String className = stack[0].getClassName();
         String methodName = stack[0].getMethodName();
         return ("java.lang.Object".equals(className) && "wait".equals(methodName))
               || ("java.lang.Thread".equals(className) && "sleep".equals(methodName))
               || ("java.net.PlainSocketImpl".equals(className) && "socketAccept".equals(methodName))
               || ("sun.misc.Unsafe".equals(className) && "park".equals(methodName))
               || ("java.net.SocketInputStream".equals(className) && "socketRead0".equals(methodName));
      }
      return true;
   }


   public static String printStackTrace(Thread thread, StackTraceElement[] trace) {
      StringBuilder sb = new StringBuilder();
      sb.append(thread + "\n");
      for (StackTraceElement element : trace)
        sb.append("\tat " + element + "\n");
      return sb.toString();
   }


   public static void interrupt(String name) {
      Thread thr = getThreadByName(name);
      thr.interrupt();
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
      Thread[] threads = new Thread[numThreads * 2];
      numThreads = group.enumerate(threads, false);

      // Enumerate each thread in `group'
      for (int i = 0; i < numThreads; i++) {
         // Get thread
         Thread thread = threads[i];
         tree.addThread(thread);
      }

      // Get thread subgroups of `group'
      int numGroups = group.activeGroupCount();
      ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
      numGroups = group.enumerate(groups, false);

      // Recursively visit each subgroup
      for (int i = 0; i < numGroups; i++) {
         ThreadGroupTree child = createTree(groups[i], level + 1);
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
