package com.finalist.cmsc.file;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class FileWalker {

   /**
    * Walk through the directory structure and return a collection containing
    * all those files for which the filter returns true
    *
    * @param startingDirectory
    *           Start walking in this directory
    * @param filter
    *           An object to determine whether or not to include this file in
    *           the returned collection
    * @return A collection of File objects
    */
   public static Collection<File> getFiles(String startingDirectory, FileFilter filter) {
      ListProcessor list = new ListProcessor();
      walk(startingDirectory, filter, list, true, false);
      return list.getList();
   }


   /**
    * Walk through the directory structure and return a collection containing
    * all those directories for which the filter returns true
    *
    * @param startingDirectory
    *           Start walking in this directory
    * @param filter
    *           An object to determine whether or not to include this directory
    *           in the returned collection
    * @return A collection of File objects
    */
   public static Collection<File> getDirectories(String startingDirectory, FileFilter filter) {
      ListProcessor list = new ListProcessor();
      walk(startingDirectory, filter, list, false, true);
      return list.getList();
   }


   /**
    * Walk through the directory structure and return a collection containing
    * all those files and directories for which the filter returns true
    *
    * @param startingDirectory
    *           Start walking in this directory
    * @param filter
    *           An object to determine whether or not to include this
    *           file/directory in the returned collection
    * @return A collection of File objects
    */
   public static Collection<File> getFilesAndDirectories(String startingDirectory, FileFilter filter) {
      ListProcessor list = new ListProcessor();
      walk(startingDirectory, filter, list, true, true);
      return list.getList();
   }


   public static void walk(String startingDirectory, FileFilter filter, FileWalkProcessor processor,
         boolean includeFiles, boolean includeDirectories) {

      if (startingDirectory == null) {
         throw new IllegalArgumentException("startingDirectory is null");
      }
      if (startingDirectory.length() == 0) {
         throw new IllegalArgumentException("startingDirectory may not be empty");
      }

      File startingDir = new File(startingDirectory);
      if (startingDir.exists() == false) {
         throw new IllegalArgumentException("startingDirectory soesn't exist");
      }
      if (startingDir.isDirectory() == false) {
         throw new IllegalArgumentException("startingDirectory not a directory");
      }

      if (includeDirectories && filter != null && filter.accept(startingDir)) {
         processor.processDirectory(startingDir);
      }
      walkDirectory(startingDir, filter, processor, includeFiles, includeDirectories);
   }


   private static void walkDirectory(File dir, FileFilter filter, FileWalkProcessor processor, boolean includeFiles,
         boolean includeDirectories) {
      final List<File> pendingDirectories = new LinkedList<File>();

      final File[] files = dir.listFiles();
      for (final File currentFile : files) {
         if (includeFiles && currentFile.isFile() && filter != null && filter.accept(currentFile)) {
            processor.processFile(currentFile);
         }
         else if (currentFile.isDirectory()) {
            if (includeDirectories && filter != null && filter.accept(currentFile)) {
               processor.processDirectory(currentFile);
            }
            pendingDirectories.add(currentFile);
         }
      }
      while (!pendingDirectories.isEmpty()) {
         File childDir = pendingDirectories.get(0);
         pendingDirectories.remove(0);
         walkDirectory(childDir, filter, processor, includeFiles, includeDirectories);
      }
   }

   static class ListProcessor implements FileWalkProcessor {
      final List<File> results = new ArrayList<File>();


      public void processDirectory(File directory) {
         results.add(directory);
      }


      public void processFile(File file) {
         results.add(file);
      }


      public List<File> getList() {
         return results;
      }

   }
}
