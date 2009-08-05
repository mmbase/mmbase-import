/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class LanguageSetImpl extends AbstractSupportSet implements LanguageSet, Serializable, Support {
   private static Log log = LogFactory.getLog(LanguageSetImpl.class);

   private ClassLoader classLoader;

   private Vector<Locale> locales;

   private boolean resourceBundleInitialized;

   private String resources;

   private String shortTitle;

   private String title;

   private String keywords;


   public LanguageSetImpl() {
      locales = new Vector<Locale>();
   }


   // create Language object with data from this class (title, short-title,
   // description, keywords)
   private Language createLanguage(Locale locale, ResourceBundle bundle) {
      LanguageImpl lang = new LanguageImpl(locale, bundle, title, shortTitle, keywords);
      return lang;
   }


   public Language get(Locale locale) {
      if (resources != null && resourceBundleInitialized == false) {
         initResourceBundle();
         this.resourceBundleInitialized = true;
      }

      if (!locales.contains(locale)) {
         locale = matchLocale(locale);
      }

      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         Language language = (Language) iterator.next();
         if (language.getLocale().equals(locale) || size() == 1) {
            return language;
         }
      }

      return null;
   }


   public Iterator<Locale> getLocales() {
      return locales.iterator();
   }


   public Locale getDefaultLocale() {
      Locale defLoc = null;

      if (locales != null && locales.size() > 0) {
         defLoc = locales.firstElement();
         if (defLoc == null) {
            defLoc = new Locale("en", "");
            locales.add(defLoc);
         }
      }
      else {
         defLoc = new Locale("en", "");
         locales.add(defLoc);
      }
      return defLoc;
   }


   public void postLoad(Object parameter) throws Exception {
      locales.addAll((Collection) parameter);
      initInlinedInfos();
      if (resources != null)
         initResourceBundle();
   }


   private void initInlinedInfos() {
      // if resource-bundle is given
      // must be initialized later when classloader is known by
      // initResourceBundle()
      if (locales.isEmpty()) {
         getDefaultLocale(); // the defualt gets automaticaly added to the
         // locals
      }
      if (keywords == null) {
         keywords = "";
      }
      if (shortTitle == null) {
         shortTitle = "";
      }
      if (title == null) {
         title = "";
      }
      add(createLanguage(getDefaultLocale(), null));
   }


   // create and add all resource bundle information as Language objects to
   // this set
   private void initResourceBundle() {
      Iterator<Locale> iter = locales.iterator();
      while (iter.hasNext()) {
         Locale locale = iter.next();
         ResourceBundle bundle = null;
         bundle = loadResourceBundle(locale);
         if (bundle != null) {
            Language language = createLanguage(locale, bundle);
            remove(language);
            add(language);
         }
      }
   }


   // try to match the given locale to a supported locale
   private Locale matchLocale(Locale locale) {
      String variant = locale.getVariant();
      if (variant != null && variant.length() > 0) {
         locale = new Locale(locale.getLanguage(), locale.getCountry());
      }

      if (!locales.contains(locale)) {
         String country = locale.getCountry();
         if (country != null && country.length() > 0) {
            locale = new Locale(locale.getLanguage(), "");
         }
      }

      if (!locales.contains(locale)) {
         locale = getDefaultLocale();
      }

      return locale;
   }


   public String getKeywords() {
      return this.keywords;
   }


   public String getResources() {
      return resources;
   }


   public String getShortTitle() {
      return this.shortTitle;
   }


   // internal methods used by castor
   public String getTitle() {
      return this.title;
   }


   // loads resource bundle files from WEB-INF/classes directory
   protected ResourceBundle loadResourceBundle(Locale locale) {
      ResourceBundle resourceBundle = null;
      try {
         if (classLoader != null) {
            resourceBundle = ResourceBundle.getBundle(resources, locale, classLoader);
         }
         else {
            resourceBundle = ResourceBundle
                  .getBundle(resources, locale, Thread.currentThread().getContextClassLoader());
         }
      }
      catch (MissingResourceException x) {
         return null;
      }
      return resourceBundle;
   }


   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }


   public void setClassLoader(ClassLoader loader) {
      this.classLoader = loader;
   }


   public void setResources(String resources) {
      this.resources = resources;
   }


   public void setShortTitle(String shortTitle) {
      this.shortTitle = shortTitle;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": ");
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         buffer.append(((LanguageImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }
}
