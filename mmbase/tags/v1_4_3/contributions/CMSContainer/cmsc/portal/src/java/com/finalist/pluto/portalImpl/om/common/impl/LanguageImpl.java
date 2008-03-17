/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.util.*;

import org.apache.pluto.om.common.Language;
import org.apache.pluto.util.Enumerator;
import org.apache.pluto.util.StringUtils;

public class LanguageImpl implements Language, java.io.Serializable {

   // ResourceBundle creation part

   private static final long serialVersionUID = 9023275378993097543L;

   private static class DefaultsResourceBundle extends ListResourceBundle {

      private Object[][] resources;


      public DefaultsResourceBundle(String defaultTitle, String defaultShortTitle, String defaultKeyWords) {
         resources = new Object[][] { { "javax.portlet.title", defaultTitle },
               { "javax.portlet.short-title", defaultShortTitle }, { "javax.portlet.keywords", defaultKeyWords } };
      }


      protected Object[][] getContents() {
         return resources;
      }
   }

   private static class ResourceBundleImpl extends ResourceBundle {

      private HashMap<String, Object> data;


      public ResourceBundleImpl(ResourceBundle bundle, ResourceBundle defaults) {
         data = new HashMap<String, Object>();

         importData(defaults);
         importData(bundle);
      }


      private void importData(ResourceBundle bundle) {
         if (bundle != null) {
            for (Enumeration<String> enumerator = bundle.getKeys(); enumerator.hasMoreElements();) {
               String key = enumerator.nextElement();
               Object value = bundle.getObject(key);
               data.put(key, value);
            }
         }
      }


      protected Object handleGetObject(String key) {
         return data.get(key);
      }


      public Enumeration<String> getKeys() {
         return new Enumerator(data.keySet());
      }
   }

   private Locale locale;

   private String title;

   private String shortTitle;

   private ResourceBundle bundle;

   private Collection<String> keywords;


   public LanguageImpl() {
      this(Locale.ENGLISH, null, "", "", "");
   }


   public LanguageImpl(Locale locale, ResourceBundle bundle, String defaultTitle, String defaultShortTitle,
         String defaultKeyWords) {
      this.bundle = new ResourceBundleImpl(bundle, new DefaultsResourceBundle(defaultTitle, defaultShortTitle,
            defaultKeyWords));

      this.locale = locale;
      title = this.bundle.getString("javax.portlet.title");
      shortTitle = this.bundle.getString("javax.portlet.short-title");
      keywords = toList(this.bundle.getString("javax.portlet.keywords"));
   }


   // Language implementation.

   public Locale getLocale() {
      return locale;
   }


   public String getTitle() {
      return title;
   }


   public String getShortTitle() {
      return shortTitle;
   }


   public Iterator<String> getKeywords() {
      return keywords.iterator();
   }


   public ResourceBundle getResourceBundle() {
      return bundle;
   }


   // internal methods.
   private List<String> toList(String value) {
      List<String> keywords = new ArrayList<String>();

      for (StringTokenizer st = new StringTokenizer(value, ","); st.hasMoreTokens();) {
         keywords.add(st.nextToken().trim());
      }

      return keywords;
   }


   public String toString() {
      return toString(0);
   }


   public String toString(final int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(":");
      StringUtils.newLine(buffer, indent);
      buffer.append("{");
      StringUtils.newLine(buffer, indent);
      buffer.append("locale='");
      buffer.append(locale);
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("title='");
      buffer.append(title);
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("shortTitle='");
      buffer.append(shortTitle);
      buffer.append("'");
      Iterator<String> iterator = keywords.iterator();
      if (iterator.hasNext()) {
         StringUtils.newLine(buffer, indent);
         buffer.append("Keywords:");
      }
      while (iterator.hasNext()) {
         buffer.append(iterator.next());
         buffer.append(',');
      }
      StringUtils.newLine(buffer, indent);
      buffer.append("}");
      return buffer.toString();
   }


   // additional methods.

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object) used for element equality
    *      according collection implementations
    */
   public boolean equals(Object o) {
      return o == null ? false : ((LanguageImpl) o).getLocale().equals(this.locale);
   }


   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      return locale.hashCode();
   }


   public void setKeywords(Collection<String> keywords) {
      this.keywords.clear();
      this.keywords.addAll(keywords);
   }


   public void setKeywords(String keywordStr) {
      if (keywords == null) {
         keywords = new ArrayList<String>();
      }
      StringTokenizer tok = new StringTokenizer(keywordStr, ",");
      while (tok.hasMoreTokens()) {
         keywords.add(tok.nextToken());
      }
   }


   public void setLocale(Locale locale) {
      this.locale = locale;
   }


   public void setShortTitle(String shortTitle) {
      this.shortTitle = shortTitle;
   }


   public void setTitle(String title) {
      this.title = title;
   }

}