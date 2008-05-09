package com.finalist.cmsc.util.bundles;

import java.util.Locale;
import java.util.Hashtable;

/**
 * Manager class which should be used in parallel with
 * InheritedPropertyResourceBundle instances. This manager will ensure that
 * instances of bundles are minimized in the JVM. The manager is also used by
 * all internal classes for the same reason.
 * 
 * @author Eric Olson <eolson@imation.com>
 * @version 1.
 */
public class InheritedBundleManager {

   /**
    * The single manager instance.
    */
   private static InheritedBundleManager instance = null;

   /**
    * The master table of all InheritedPropertyResourceBundles which have been
    * created and cached.
    */
   private Hashtable<InheritedBundleKey, InheritedPropertyResourceBundle> bundles = null;


   /**
    * The static method for returning the singleton instance.
    * 
    * @return the singleton manager instance.
    */
   public static InheritedBundleManager getInstance() {
      if (instance == null) {
         // create the only instance of this class.
         instance = new InheritedBundleManager();
      }

      return instance;
   }


   /**
    * Constructs a new manager.
    */
   protected InheritedBundleManager() {
      bundles = new Hashtable<InheritedBundleKey, InheritedPropertyResourceBundle>();

   }


   /**
    * Gets the appropriate InheritedPropertyResourceBundle. If the bundle does
    * not already exist in the cache, a new one is created and stored.
    * 
    * @param baseName
    *           the resource bundle base name.
    * @param locale
    *           the locale. This is optional. If no locale is specified, the
    *           default locale is used.
    * @param loader
    *           the class loader used to locate resource bundles and the
    *           relationships file. This is optional. If no class loader is
    *           specified, the system class loader is used.
    * @return the resource bundle.
    */
   public InheritedPropertyResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {

      // create a new key
      InheritedBundleKey key = new InheritedBundleKey(baseName, locale, loader);

      // get the instance from the table.
      InheritedPropertyResourceBundle bundle = bundles.get(key);

      // the instance has not yet been created. Create a new one
      // and cache it.
      if (bundle == null) {
         bundle = new InheritedPropertyResourceBundle(baseName, locale, loader);
         bundles.put(key, bundle);
      }

      return bundle;
   }


   /**
    * Gets the appropriate InheritedPropertyResourceBundle. If the bundle does
    * not already exist in the cache, a new one is created and stored.
    * 
    * @param baseName
    *           the resource bundle base name.
    * @param locale
    *           the locale. This is optional. If no locale is specified, the
    *           default locale is used.
    * @return the resource bundle.
    */

   public InheritedPropertyResourceBundle getBundle(String baseName, Locale locale) {

      return getBundle(baseName, locale, null);
   }


   /**
    * Gets the appropriate InheritedPropertyResourceBundle. If the bundle does
    * not already exist in the cache, a new one is created and stored.
    * 
    * @param baseName
    *           the resource bundle base name.
    * @return the resource bundle.
    */
   public InheritedPropertyResourceBundle getBundle(String baseName) {

      return getBundle(baseName, null);
   }
}

/**
 * The key used in the manager table.
 */
class InheritedBundleKey {

   /**
    * The bundle base name.
    */
   private String baseName = null;

   /**
    * The locale.
    */
   private Locale locale = null;

   /**
    * The class loader.
    */
   private ClassLoader loader = null;


   /**
    * Creates a new bundle key.
    * 
    * @param baseName
    *           the bundle base name.
    * @param locale
    *           the locale.
    * @param loader
    *           the class loader.
    */
   InheritedBundleKey(String baseName, Locale locale, ClassLoader loader) {
      this.baseName = baseName;
      this.locale = locale;
      this.loader = loader;
   }


   @Override
   public boolean equals(Object o) {
      if (o instanceof InheritedBundleKey) {
         InheritedBundleKey other = (InheritedBundleKey) o;

         // determine if locales are equal. These are equal if both are
         // null, or both are not null, and equals() returns true.
         if (locale == null) {
            if (other.locale != null)
               return false;
         }
         else {
            if (other.locale == null) {
               return false;
            }
            else {
               if (!locale.equals(other.locale)) {
                  return false;
               }
            }
         }

         // determine if loaders are equal. These are equal if both are
         // null, or both are not null, and equals() returns true.
         if (loader == null) {
            if (other.loader != null)
               return false;
         }
         else {
            if (other.loader == null) {
               return false;
            }
            else {
               if (!loader.equals(other.loader)) {
                  return false;
               }
            }
         }

         // determine if bundle base names are equal.
         return (baseName.equals(other.baseName));
      }
      return false;
   }


   @Override
   public int hashCode() {
      int retVal = 0;

      // XOR all hashCodes for the three instance variables.
      if (baseName != null)
         retVal = retVal | baseName.hashCode();
      if (locale != null)
         retVal = retVal | locale.hashCode();
      if (loader != null)
         retVal = retVal | loader.hashCode();

      return retVal;
   }
}
