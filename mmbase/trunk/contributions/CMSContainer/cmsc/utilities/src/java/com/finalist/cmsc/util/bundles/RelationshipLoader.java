package com.finalist.cmsc.util.bundles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Locale;
import java.util.Vector;
import java.util.Enumeration;

/**
 * This class is used to load the relationships file.
 * 
 * @author Eric Olson <eolson@imation.com>
 * @version 1.
 */
public class RelationshipLoader {

   /**
    * The resource bundle base name.
    */
   private String baseName = null;

   /**
    * The locale which will be used for all created parent bundles. If this is
    * left null, the default locale is used.
    */
   private Locale locale = null;

   /**
    * The class loader which will be used for all created parent bundles. If
    * this is left null, the system class loader is used.
    */
   private ClassLoader loader = null;

   /**
    * The parent bundles loaded from the relationships file.
    */
   private Vector<InheritedPropertyResourceBundle> relationships = null;

   /**
    * The file extensions which is searched on to obtain the relationships file.
    */
   public static final String FILE_EXTENSION = ".relationships";

   /**
    * The character which is used to replace the '.' character in resource
    * names.
    */
   private static final char RESOURCE_SEPARATOR = '/';
   // private static final char RESOURCE_SEPARATOR = File.separatorChar;

   /**
    * The string which denotes a comment in the relationships file.
    */
   public static final String COMMENT_ID = "#";


   /**
    * Creates a new RelationshipLoader.
    * 
    * @param baseName
    *           the base resource name.
    * @param locale
    *           the locale. This is optional. If no locale is specified the
    *           default locale is used.
    * @param loader
    *           the class loader. This is optional. If no class loader is
    *           specified, the system class loader is used.
    */
   protected RelationshipLoader(String baseName, Locale locale, ClassLoader loader) {
      this.baseName = baseName;
      this.locale = locale;
      this.loader = loader;

      initRelationships();
   }


   /**
    * Initializes all parent relationships defined in the relationships file.
    * Subsequent to this method call, the createRelationships() method can be
    * called to set up the parents in the resource bundle.
    */
   protected void initRelationships() {

      // all parent bundles defined in the relationships file.
      relationships = new Vector<InheritedPropertyResourceBundle>();

      // gets all parent bundle names from the file.
      Enumeration<String> e = getRelationshipNames();

      while (e.hasMoreElements()) {

         String curName = e.nextElement();

         // Gets the InheritedPropertyResourceBundle from the
         // manager with parent bundle name and the locale
         // and class loader used to initialize the bundle.
         // Then adds it to the list of parent bundles.
         relationships.add(InheritedBundleManager.getInstance().getBundle(curName, locale, loader));

      }
   }


   /**
    * Adds relationships to the InheritedPropertyResourceBundle.
    * 
    * @param source
    *           the InheritedPropertyResourceBundle.
    */
   protected void createRelations(InheritedPropertyResourceBundle source) {
      for (Object element : relationships) {
         InheritedPropertyResourceBundle cur = (InheritedPropertyResourceBundle) element;

         source.addRelationship(cur);

      }
   }


   /**
    * Retrieves the relationships file from the given ClassLoader and parses the
    * information.
    * 
    * @return all resource bundle names defined in the relationships file.
    */
   private Enumeration<String> getRelationshipNames() {

      Vector<String> retVal = new Vector<String>();

      InputStream is = null;
      InputStreamReader isr = null;
      BufferedReader br = null;

      try {
         // convert the resource base name to the file system
         // equivalent.
         String rName = baseName.replace('.', RESOURCE_SEPARATOR);
         rName += FILE_EXTENSION;

         // retrieve the resource from the class loader. If no class
         // loader is defined, the system class loader is used.
         if (loader == null) {
            is = ClassLoader.getSystemResourceAsStream(rName);
         }
         else {
            is = loader.getResourceAsStream(rName);
         }

         // no relationships file was found, return.
         if (is == null)
            return retVal.elements();

         isr = new InputStreamReader(is);
         br = new BufferedReader(isr);

         String line = null;

         while ((line = br.readLine()) != null) {
            if (!line.startsWith(COMMENT_ID)) {
               line = line.trim();
               if (!line.equals("")) {
                  retVal.add(line);
               }
            }
         }

         return retVal.elements();

      }
      catch (IOException ioe) {
         // do nothing. We will try to clean up streams in the finally clause.
         // Reading of the streams will halt.
      }
      finally {
         try {
            if (br != null)
               br.close();
            if (isr != null)
               isr.close();
            if (is != null)
               is.close();
         }
         catch (IOException ioe2) {
            // this is very bad. System resources could not be cleaned up.
            // In production level code, this should probably either throw
            // some sort of RuntimeException, or log the error to an
            // application log.
            ioe2.printStackTrace();
         }
      }

      return null;
   }
}
