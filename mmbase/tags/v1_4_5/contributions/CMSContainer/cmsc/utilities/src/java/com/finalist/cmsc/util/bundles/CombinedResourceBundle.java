/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.util.bundles;

import java.util.*;

public class CombinedResourceBundle extends ResourceBundle {

   /**
    * Describes the Bundless to other ResourceBundles.
    */
   private List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();;


   public CombinedResourceBundle(ResourceBundle rb) {
      bundles.add(rb);
   }


   /**
    * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
    */
   @Override
   protected Object handleGetObject(String key) {
      Object retVal = null;

      // resource not found in PropertyResourceBundle, check all parents.
      for (Iterator<ResourceBundle> iter = bundles.iterator(); iter.hasNext();) {
         ResourceBundle rb = iter.next();
         try {
            retVal = rb.getObject(key);
         }
         catch (MissingResourceException mre) {
            // Do nothing here. This will cause a search in the following
            // bundles.
         }
         if (retVal != null) {
            return retVal;
         }
      }
      throw new MissingResourceException(
            "Can't find resource for bundle " + this.getClass().getName() + ", key " + key, this.getClass().getName(),
            key);
   }


   /**
    * @see java.util.ResourceBundle#getKeys()
    */
   @Override
   public Enumeration<String> getKeys() {
      Vector<String> temp = new Vector<String>();

      // add all keys from parent bundles if they exist.
      for (Iterator<ResourceBundle> iter = bundles.iterator(); iter.hasNext();) {
         ResourceBundle rb = iter.next();
         // get all keys for this parent.
         for (Enumeration<String> g = rb.getKeys(); g.hasMoreElements();) {
            String k = g.nextElement();
            // only add the key if it does not already exist.
            if (!temp.contains(k)) {
               temp.add(k);
            }
         }
      }
      return temp.elements();
   }


   /**
    * Adds the ResourceBundle at the end of the bundles list.
    * 
    * @param r
    *           the resource bundle which will be added to the end of the
    *           bundles list.
    */
   public void addBundles(ResourceBundle r) {
      bundles.add(r);
   }


   /**
    * Adds the ResourceBundle at the given location. All subsequent bundles will
    * be pushed down one space.
    * 
    * @param r
    *           the resource bundle which will be added.
    * @param l
    *           the insertion location of the resource bundle. For example, a
    *           value of 0 will place this parent bundle at the beginning of the
    *           list - it will be checked for any key first.
    */
   public void addBundles(ResourceBundle r, int l) {
      bundles.add(l, r);
   }


   /**
    * Removes the resource bundle from the parent list.
    * 
    * @param r
    *           resource bundle to remove
    */
   public void removeBundles(ResourceBundle r) {
      bundles.remove(r);
   }


   /**
    * Get all resource bundles in this combined version
    * 
    * @return list of resourcebundles
    */
   public List<ResourceBundle> getBundles() {
      return bundles;
   }

}