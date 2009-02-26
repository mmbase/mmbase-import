package com.finalist.cmsc.util.bundles;

import java.util.*;

/**
 * The class for representing an inherited PropertyResourceBundle. This class
 * incorporates all the features of the java.util.PropertyResourceBundle while
 * adding the notion of inheritance.
 * <p>
 * The inheritance relationships can be defined in-line, or they can be provided
 * in a relationships file which is locatable by the given ClassLoader. This
 * file must be named <resource_base_name>.relationships. The format for this
 * file is simply the list of parent bundles in the order of precedence.
 * Comments (lines starting with the '#' character,) and blank lines are
 * permitted.
 * <p>
 * <strong>Example:</strong><br>
 *
 * <pre>
 *  # Relationships file for the resources.ProductNames bundle.
 * # resources/ProductNames.relationships
 *
 * resources.CompanyStandards
 * resources.Products
 * </pre>
 *
 * <p>
 * In the above example, if a key is searched for that does not exist in the
 * backed PropertyResourceBundle, then the resources.CompanyStandards bundle is
 * checked first. If it is not found there, the resources.Products bundle is
 * checked next.
 *
 * @see java.util.PropertyResourceBundle
 * @author Eric Olson <eolson@imation.com>
 * @version 1.
 */
public class InheritedPropertyResourceBundle extends ResourceBundle {

   /**
    * Describes the parent relationships to other
    * InheritedPropertyResourceBundles. These parent bundles will be checked if
    * any key cannot be found in this bundle.
    */
   private List<InheritedPropertyResourceBundle> relationships = null;

   /**
    * The java.util.ResourceBundle that backs this instance. This bundle will
    * always be checked first for any key.
    */
   private ResourceBundle instance = null;

   /**
    * The resource bundle name.
    */
   private String baseName = null;

   /**
    * The locale for this resource bundle. This is optional. If no locale is
    * specified then the default locale is used.
    */
   private Locale locale = null;

   /**
    * The ClassLoader used to look for resource bundles. This is optional. If no
    * ClassLoader is specified, then the system class loader is used.
    */
   private ClassLoader loader = null;


   /**
    * Creates a new InheritedPropertyResourceBundle. This constructor should
    * only be called by a resource bundle manager so that duplicate versions are
    * not created.
    *
    * @param baseName
    *           the base name of the resource bundle.
    * @param locale
    *           the locale for which the resource bundle will be loaded. This is
    *           optional. If no locale is provided, the default locale is used.
    * @param loader
    *           the ClassLoader which is used to find the resource bundle and
    *           relationships files.
    */
   protected InheritedPropertyResourceBundle(String baseName, Locale locale, ClassLoader loader) {
      super();
      // cache all creation information.
      this.baseName = baseName;
      this.locale = locale;
      this.loader = loader;

      // create the backed PropertyResourceBundle with the appropriate
      // context.
      if (this.locale == null) {
         this.instance = ResourceBundle.getBundle(this.baseName);
      }
      else {
         if (this.loader == null) {
            this.instance = ResourceBundle.getBundle(this.baseName, this.locale);
         }
         else {
            this.instance = ResourceBundle.getBundle(this.baseName, this.locale, this.loader);
         }
      }

      initRelationships();
   }


   /**
    * Initializes the parent relationships from the relationships file.
    *
    * @see RelationshipLoader
    */
   protected final void initRelationships() {

      // create a RelationshipLoader with the appropriate context.
      RelationshipLoader rl = new RelationshipLoader(baseName, locale, loader);

      relationships = new ArrayList<InheritedPropertyResourceBundle>();

      // add the parent relationships to this instance.
      rl.createRelations(this);
   }


   /**
    * Returns an enumeration of keys this resource bundle knows about. This will
    * return all keys - including keys actually defined in parent bundles.
    */
   @Override
   public Enumeration<String> getKeys() {
      List<String> temp = new ArrayList<String>();

      // get all keys for the backed PropertyResourceBundle
      for (Enumeration<String> e = instance.getKeys(); e.hasMoreElements();) {
         temp.add(e.nextElement());
      }

      // add all keys from parent bundles if they exist.
      if (relationships != null) {
         for (Object element : relationships) {

            InheritedPropertyResourceBundle iprb = (InheritedPropertyResourceBundle) element;

            // get all keys for this parent.
            for (Enumeration<String> g = iprb.getKeys(); g.hasMoreElements();) {

               String k = g.nextElement();

               // only add the key if it does not already exist.
               if (!temp.contains(k)) {
                  temp.add(k);
               }
            }
         }
      }

      return Collections.enumeration(temp);
   }


   /**
    * Gets an object from this resource bundle. If the resource is not found in
    * the backing PropertyResourceBundle, then parent bundles are searched, in
    * order, for the resource.
    *
    * @param key
    *           the key of the desired resource.
    * @return the resource corresponding to the key, or null if the resource
    *         could not be found.
    */
   @Override
   protected Object handleGetObject(String key) {
      Object retVal = null;

      try {
         // check the PropertyResourceBundle for the resource.
         retVal = instance.getObject(key);
      }
      catch (MissingResourceException mre) {
         // Do nothing here. This will cause a search of the parents
         // of this inherited bundle.
      }

      if (retVal == null && relationships != null) {
         // resource not found in PropertyResourceBundle, check all parents.
         for (Object element : relationships) {

            // attempt to get the resource from the current parent bundle.
            retVal = ((InheritedPropertyResourceBundle) element).handleGetObject(key);

            if (retVal != null) {
               return retVal;
            }
         }
      }

      return retVal;
   }


   /**
    * Adds the InheritedPropertyResourceBundle at the end of the parent list.
    *
    * @param r
    *           the resource bundle which will be added to the end of the parent
    *           list.
    */
   public void addRelationship(InheritedPropertyResourceBundle r) {
      relationships.add(r);

   }


   /**
    * Adds the InheritedPropertyResourceBundle at the given location. All
    * subsequent parents will be pushed down one space.
    *
    * @param r
    *           the resource bundle which will be added.
    * @param l
    *           the insertion location of the resource bundle. For example, a
    *           value of 0 will place this parent bundle at the beginning of the
    *           list - it will be checked for any key first.
    */
   public void addRelationship(InheritedPropertyResourceBundle r, int l) {
      relationships.add(l, r);
   }


   /**
    * Removes the resource bundle from the parent list.
    *
    * @param r
    *           resource bundle to remove
    */
   public void removeRelationship(InheritedPropertyResourceBundle r) {
      relationships.remove(r);
   }


   /**
    * Gets the parent bundles associated with this instance.
    *
    * @return an Enumeration of the parent resource bundles.
    */
   public Enumeration<InheritedPropertyResourceBundle> getRelationships() {
      if (relationships == null) {
         return Collections.enumeration(new ArrayList<InheritedPropertyResourceBundle>());
      }
      return Collections.enumeration(relationships);
   }
}
