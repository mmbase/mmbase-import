/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.mojo;

import java.io.*;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Create INDEX files inside the org.mmmbase.config package
 *
 * @goal create-index-files
 * @phase process-classes
 */
public class CreateIndexFiles extends AbstractMojo {

   /**
    * Directory containing the classes and resource files that should be packaged into the JAR.
    *
    * @parameter expression="${project.build.outputDirectory}"
    * @required
    */
   private File classesDirectory;

   private FilenameFilter dirs = new FilenameFilter() {
      public boolean accept(File dir, String name) {
         return new File(dir, name).isDirectory();
      }
   };

   public void execute() {
      if (classesDirectory != null && classesDirectory.exists()) {
         File configDir = new File(classesDirectory, File.separator + "org" + File.separator
                                   + "mmbase" + File.separator + "config");
         if (configDir.exists()) {
            createIndex(configDir);
         }
      }
   }

   protected void createIndex(File dir) {
      if (dir.isDirectory()) {
         File index = new File(dir, "INDEX");
         if (index.exists()) {
            index.delete();
         }
         writeIndex(dir, index);

         String subdirs[] = dir.list(dirs);
         for (int i = 0; i < subdirs.length; i++) {
            createIndex(new File(dir, subdirs[i]));
         }
      }
   }

   private void writeIndex(File dir, File index) {
      getLog().debug("Generating " + index);
      BufferedWriter w = null;
      try {
         w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(index), "UTF-8"));
         String files[] = dir.list();
         for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            w.write(files[i]);
            if (file.isDirectory()) {
               w.write('/');
            }
            w.newLine();
         }
      } catch (UnsupportedEncodingException e) {
         getLog().error(e);
      } catch (FileNotFoundException e) {
         getLog().error(e);
      } catch (IOException e) {
         getLog().error(e);
      } finally {
         try {
             if (w != null) {
               w.close();
            }
         } catch (IOException e) {
            getLog().debug("" + e.getMessage(), e);
         }
      }
   }

}
