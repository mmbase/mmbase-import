/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.application.wordfilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Properties;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import xmlbs.PropertiesDocumentStructure;

/**
 *
 * This util class removes ugly html code from a string
 * Ugly html code could be a result of copy&paste from 
 * ms word to the mmbase editwizards wysiwyg input
 * 
 * @author Nico Klasens (Finalist IT Group)
 * 
 * @version $Revision: 1.1 $
 */
public class WordHtmlCleaner {

   /** MMBase logging system */
   private static Logger log =
      Logging.getLoggerInstance(WordHtmlCleaner.class.getName());

   /** xmlbs stuff
    *
    * Document structure configurable using a property file.  A
    * property key is a tag name, when it starts with a <tt>_</tt>
    * character a includable set, <tt>&#64;ROOT</tt> when it
    * denotes the document root and <tt>&amp;</tt> for a list of all
    * known entities.  Property values give a list of tags which can
    * be parents of the given key, when a value starts with
    * <tt>$</tt> it denotes a attribute name and a value starting
    * with <tt>_</tt> references an other property.
    */
   public static PropertiesDocumentStructure xmlbsDTD = null;

   static {
      Properties prop = new Properties();
      prop.setProperty("@ROOT", "body");
      prop.setProperty("body", "_all");

      prop.setProperty("_all", "_style table");
      prop.setProperty("_style", "strong p a b i u ul ol #TEXT br");

      prop.setProperty("p", "_style");
      prop.setProperty("b", "_style");
      prop.setProperty("i", "_style");
      prop.setProperty("u", "_style");
      prop.setProperty("strong", "_style");

      prop.setProperty("a", "_style $href");
      prop.setProperty("ul", "li ul ol");
      prop.setProperty("ol", "li ul ol");
      prop.setProperty("li", "_style");
      prop.setProperty("br", "");

      prop.setProperty("table", "tr $width $height $border $cellspacing $cellpadding");
      prop.setProperty("tr", "td th $colspan $rowspan");
      prop.setProperty("td", "_cell");
      prop.setProperty("th", "_cell");

      prop.setProperty("_cell", "_all $colspan $rowspan");
      //entities
      prop.setProperty("&", "nbsp");

      xmlbsDTD = new xmlbs.PropertiesDocumentStructure(prop);
      xmlbsDTD.setIgnoreCase(true);
   }

   /** Cleans html code
    *
    * @param textStr ugly html code
    * @return clean html code
    */
   public static String cleanHtml(String textStr) {
      try {
         xmlbs.XMLBS xmlbs =
            new xmlbs.XMLBS("<body>" + textStr + "</body>", xmlbsDTD);
         xmlbs.process();
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         xmlbs.write(bout);
         bout.flush();
         String xmlStr = bout.toString();

         // to string and strip root node
         xmlStr = xmlStr.substring(xmlStr.indexOf('>') + 1);
         int i = xmlStr.lastIndexOf('<');
         if (i != -1) {
            xmlStr = xmlStr.substring(0, i);
         }

         xmlStr = removeXmlNamespace(xmlStr);

         return xmlStr;
      }
      catch (IllegalStateException e) {
         log.error("Clean html failed");
         log.error(Logging.stackTrace(e));
      }
      catch (IOException e) {
         log.error("Clean html failed");
         log.error(Logging.stackTrace(e));
      }
      return "";
   }

   /** remove xml namespace declarations
    * @param xmlStr xml string
    * @return xml string with namespace removed
    */
   private static String removeXmlNamespace(String xmlStr) {
       if (xmlStr == null) {
          return xmlStr;
       }
       else {
          if (xmlStr.length() < 13 ) {
             return xmlStr;
          }
          else {
             String xml = null;
             int begin = 0;
             int end = 0;
             while ((begin = xmlStr.indexOf("&lt;?xml", end)) > -1) {
                 xml += xmlStr.substring(end, begin);
                 end = xmlStr.indexOf("/&gt;", begin);
                 if (end > -1) {
                    end += 5;
                 }
                 else {
                    xml += "&lt;?xml";
                    end = begin + 8;
                 }
             }
       
             if (end < xmlStr.length()) {
                xml += xmlStr.substring(end);
             }
             return xml;
          }
       }
   }

}