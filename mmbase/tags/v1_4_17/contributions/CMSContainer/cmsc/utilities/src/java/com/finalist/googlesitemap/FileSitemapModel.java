/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.googlesitemap;

import java.io.File;
import java.util.*;

public class FileSitemapModel implements SitemapModel {

   private String startingDirectory;
   private String startingUrl;


   public FileSitemapModel(String startingDirectory, String startingUrl) {
      this.startingDirectory = startingDirectory;
      this.startingUrl = startingUrl;
   }


   public Object getRoot() {
      if (startingDirectory == null) {
         throw new NullPointerException();
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

      return startingDir;
   }


   public List<File> getChildren(Object root) {
      List<File> children = new ArrayList<File>();
      if (root instanceof File) {
         Collections.addAll(children, ((File) root).listFiles());
      }
      return children;
   }


   public boolean isUrl(Object root) {
      if (root instanceof File) {
         File file = ((File) root);
         if (!file.isDirectory())
            return file.getName().endsWith(".htm") || file.getName().endsWith(".html")
                  || file.getName().endsWith(".shtml");
      }
      return false;
   }


   public String getLocation(Object root) {
      if (root instanceof File) {
         String path = ((File) root).getPath();
         return startingUrl + path.substring(startingDirectory.length()).replace('\\', '/');
      }
      return null;
   }


   public Date getLastModified(Object root) {
      if (root instanceof File) {
         return new Date(((File) root).lastModified());
      }
      return null;
   }


   public String getChangeFrequency(Object root) {
      return null;
   }

}
