/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.wordfilter;

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
 * @version $Revision: 1.3 $
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
      prop.setProperty("_style", "strong h3 h2 h1 p a b i u ul ol #TEXT br");
      
      prop.setProperty("h3", "_style");
      prop.setProperty("h2", "_style");
      prop.setProperty("h1", "_style");
      
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
      if (textStr != null) {

         try {
            //The font tag is required to fix wordpad anchor links
            // xmlbs removes the fonttags
            String xmlStr = fixWordpad(textStr);
            xmlStr = fixLists(xmlStr);
            
            xmlbs.XMLBS xmlbs =
               new xmlbs.XMLBS("<body>" + xmlStr + "</body>", xmlbsDTD);
            xmlbs.process();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            xmlbs.write(bout);
            bout.flush();
            xmlStr = bout.toString();
   
            // to string and strip root node
            xmlStr = xmlStr.substring(xmlStr.indexOf('>') + 1);
            int i = xmlStr.lastIndexOf('<');
            if (i != -1) {
               xmlStr = xmlStr.substring(0, i);
            }
   
            xmlStr = replaceParagraph(xmlStr);
            xmlStr = removeXmlNamespace(xmlStr);
            xmlStr = fixAnchors(xmlStr);
            xmlStr = removeEmptyTags(xmlStr);
   
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
      }
      return "";
   }

   /** remove xml namespace declarations
    * @param text xml string
    * @return xml string with namespace removed
    */
   private static String removeXmlNamespace(String text) {
      text = text.replaceAll("<\\?xml:namespace.*?/>", "");
      text = text.replaceAll("\\&lt;\\?xml:namespace.*?/\\&gt;", "");
      return text;
   }
   
   private static String replaceParagraph(String text) {
      // remove all remaining <p>
      text = text.replaceAll("<\\s*[pP]{1}\\s*.*?>","");
      // replace all remaining </p> with a <br><br>
      text = text.replaceAll("<\\s*/[pP]{1}\\s*.*?>","<BR><BR>");
      // remove all <br> at the end
      text = text.replaceAll("(<\\s*[bB]{1}[rR]{1}\\s*[^>]*?>(\\s|(&nbsp;))*)*\\z","");
      return text;
   }
   
   /** Fixes the anchors tags for Wordpad: <U><FONT color=#0000ff> ... </U></FONT>
    *  
    * @param xmlStr xml string
    * @return xml string with fixed anchors
    */
   private static String fixWordpad(String xmlStr) {
      String xml = "";
      int begin = 0;
      int end = 0;
      while ((begin = xmlStr.indexOf("<U><FONT color=#0000ff>", end)) > -1) {
         xml += xmlStr.substring(end, begin);
         end = xmlStr.indexOf("</U></FONT>", begin);
         if (end > -1) {
            String link = xmlStr.substring(begin + "<U><FONT color=#0000ff>".length(), end);
            xml += "<a href=\"" + stripHtml(link) + "\">" + link + "</a>";
            end += "</U></FONT>".length();
         }
         else {
            xml += "<U><FONT color=#0000ff>";
            end = begin + "<U><FONT color=#0000ff>".length();
         }
      }
    
      if (end < xmlStr.length()) {
         xml += xmlStr.substring(end);
      }
      return xml;
   }

   private static String fixLists(String xmlStr) {
      String xml = "";
      int begin = 0;
      int end = 0;
      while ((begin = xmlStr.indexOf("<LI>", end)) > -1) {
         if (begin != end) {
            xml += xmlStr.substring(end, begin);
         }
         
         end = xmlStr.indexOf("</LI>", begin);
         if (end > -1) {
            end += "</LI>".length();
            xml += xmlStr.substring(begin, end);
         }
         else {
            end = xmlStr.indexOf("<LI>", begin + "<LI>".length());
            if (end == -1) {
               end = xmlStr.length();
            }

            int endList = xmlStr.indexOf("</OL>", begin);
            if (endList == -1) {
               endList = xmlStr.indexOf("</UL>", begin);
               if (endList == -1) {
                  endList = xmlStr.length();
               }
            }
            
            if (end <= endList) {
               xml += xmlStr.substring(begin, end) + "</LI>";
               end -= 1;
            }
            else {
               if (end > endList) {
                  xml += xmlStr.substring(begin, endList) + "</LI>";
                  end = endList;
                  if (endList != xmlStr.length()) {
                     xml += xmlStr.substring(endList, (endList + "</OL>".length()));
                     end += "</OL>".length();
                  }
               }
            }
         }
      }
      if (end < xmlStr.length()) {
         xml += xmlStr.substring(end);
      }
      return xml;
   }
   
   /** Fixes the anchors tags
    *  
    * @param xmlStr xml string
    * @return xml string with fixed anchors
    */
   private static String fixAnchors(String xmlStr) {
      String xml = "";
      int begin = 0;
      int end = 0;
      while ((begin = xmlStr.indexOf("<A", end)) > -1) {
         xml += xmlStr.substring(end, begin);
         int endBegin = xmlStr.indexOf(">", begin);
         end = xmlStr.indexOf("</A>", begin);
         if (end > -1 && "".equals(stripHtml(xmlStr.substring(endBegin+1, end)))) {
            String atag = xmlStr.substring(begin, endBegin + 1);
            int hrefBegin = atag.indexOf("href=\"");
            if (hrefBegin > -1) {
               hrefBegin += "href=\"".length();
               int hrefEnd = atag.indexOf("\"",hrefBegin);
               xml += atag + atag.substring(hrefBegin, hrefEnd) + "</A>";
            }
            end += "</A>".length();
         }
         else {
            end += "</A>".length();
            xml += xmlStr.substring(begin, end);
         }
      }
      if (end < xmlStr.length()) {
         xml += xmlStr.substring(end);
      }
      return xml;
   }

   private static String removeEmptyTags(String text) {
      return text.replaceAll("<[bBiIuU]\\s*/>", "");
   }

   
   private static String stripHtml(String text) {
      return text.replaceAll("<.*>", "");
   }
   
}