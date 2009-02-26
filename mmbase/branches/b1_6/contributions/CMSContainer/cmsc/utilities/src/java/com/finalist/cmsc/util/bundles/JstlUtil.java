/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.util.bundles;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

public class JstlUtil {

   public static Locale getLocale(HttpServletRequest request) {
      Locale locale = (Locale) Config.get(request, Config.FMT_LOCALE);
      if (locale == null) {
         locale = (Locale) Config.get(request.getSession(), Config.FMT_LOCALE);
         if (locale == null) {
            locale = (Locale) Config.get(request.getSession().getServletContext(), Config.FMT_LOCALE);
            if (locale == null) {
               locale = request.getLocale();
            }
         }
      }

      return locale;
   }


   public static String getMessage(HttpServletRequest request, String key) {
      ResourceBundle rb = getResourceBundle(request);
      return rb.getString(key);
   }


   public static String getMessage(String baseName, Locale locale, String key) {
      ResourceBundle rb = getResourceBundle(baseName, locale);
      return rb.getString(key);
   }


   private static ResourceBundle getResourceBundle(String baseName, Locale locale) {
      ResourceBundle rb = ResourceBundle.getBundle(baseName, locale);
      return rb;
   }


   public static ResourceBundle getResourceBundle(HttpServletRequest request) {
      LocalizationContext i18nContext = (LocalizationContext) Config.get(request, Config.FMT_LOCALIZATION_CONTEXT);
      ResourceBundle rb = i18nContext.getResourceBundle();
      return rb;
   }


   public static void setResourceBundle(HttpServletRequest request, String name) {
      Locale locale = getLocale(request);
      ResourceBundle bundle = ResourceBundle.getBundle(name, locale);
      setResourceBundle(request, bundle);
   }


   public static void setResourceBundles(HttpServletRequest request, String[] names) {
      Locale locale = getLocale(request);
      CombinedResourceBundle cbundle = null;

      for (String name : names) {
         ResourceBundle bundle = ResourceBundle.getBundle(name, locale);
         if (cbundle == null) {
            cbundle = new CombinedResourceBundle(bundle);
         }
         else {
            cbundle.addBundles(bundle);
         }
      }
      setResourceBundle(request, cbundle);
   }


   public static void setResourceBundle(ServletRequest request, ResourceBundle bundle) {
      LocalizationContext ctx = new LocalizationContext(bundle);
      Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, ctx);
   }

}
