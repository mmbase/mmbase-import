/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.googlesitemap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public class Generator {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(Generator.class.getName());

   private static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/googlesitemap/generator.properties";

   private String googleNamespace = null;
   private String lastmod = null;
   private String changefreq = null;
   private SimpleDateFormat dateTimeFormatter;


   public Generator() {
      try {
         Properties p = new Properties();
         p.load(Generator.class.getResourceAsStream(CONFIGURATION_RESOURCE_NAME));

         googleNamespace = p.getProperty("googlens");
         if (googleNamespace == null) {
            throw new IllegalArgumentException("googlens missing");
         }

         String dateTimeFormat = p.getProperty("datetimeformat");
         setDateFormat(dateTimeFormat);

         String changefreq = p.getProperty("changefreq");
         setChangefreq(changefreq);
      }
      catch (IOException e) {
         log.warn("No " + CONFIGURATION_RESOURCE_NAME + " configuration found");
      }
   }


   public final void setDateFormat(String dateTimeFormat) {
      if (dateTimeFormat == null) {
         dateTimeFormat = "yyyy-MM-dd";
      }
      dateTimeFormatter = new SimpleDateFormat(dateTimeFormat);
      lastmod = dateTimeFormatter.format(new Date());
   }


   public final void setChangefreq(String changefreq) {
      if (changefreq == null) {
         this.changefreq = "monthly";
      }
      else {
         this.changefreq = changefreq;
      }
   }


   public String generate(SitemapModel model) {
      Document sitemap = XmlUtil.createDocument();
      Element urlsetNode = XmlUtil.createRoot(sitemap, "urlset", googleNamespace);

      Object root = model.getRoot();
      addUrls(model, urlsetNode, root);

      return XmlUtil.serializeDocument(sitemap);
   }


   private void addUrls(SitemapModel model, Element urlsetNode, Object root) {
      if (model.isUrl(root)) {
         String loc = model.getLocation(root);
         if (loc != null) {
            String lastmodStr = lastmod;
            Date lastmodDate = model.getLastModified(root);
            if (lastmodDate != null) {
               lastmodStr = dateTimeFormatter.format(lastmodDate);
            }
            String changefreq = model.getChangeFrequency(root);
            if (changefreq == null) {
               changefreq = this.changefreq;
            }
            addUrlElement(urlsetNode, loc, lastmodStr, changefreq);
         }
      }

      List<File> children = model.getChildren(root);
      if (children != null) {
         for (Object child : children) {
            addUrls(model, urlsetNode, child);
         }
      }
   }


   private Element addUrlElement(Element urlsetNode, final String loc, final String lastmod, final String changefreq) {
      Element urlElement = XmlUtil.createChild(urlsetNode, "url");
      XmlUtil.createChildText(urlElement, "loc", loc);
      XmlUtil.createChildText(urlElement, "lastmod", lastmod);
      XmlUtil.createChildText(urlElement, "changefreq", changefreq);
      return urlElement;
   }

}
