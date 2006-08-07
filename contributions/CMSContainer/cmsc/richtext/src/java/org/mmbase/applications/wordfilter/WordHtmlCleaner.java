/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.applications.wordfilter;

import java.io.*;

import java.util.Properties;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import xmlbs.PropertiesDocumentStructure;

/**
 * 
 * This util class removes ugly html code from a string Ugly html code could be
 * a result of copy&paste from ms word to the mmbase editwizards wysiwyg input
 * 
 * @author Nico Klasens (Finalist IT Group)
 * 
 * @version $Revision: 1.2 $
 */
public class WordHtmlCleaner {

   /** MMBase logging system */
   private static Logger log = Logging.getLoggerInstance(WordHtmlCleaner.class
         .getName());

   /**
    * xmlbs stuff
    * 
    * Document structure configurable using a property file. A property key is a
    * tag name, when it starts with a <tt>_</tt> character a includable set,
    * <tt>&#64;ROOT</tt> when it denotes the document root and <tt>&amp;</tt>
    * for a list of all known entities. Property values give a list of tags
    * which can be parents of the given key, when a value starts with <tt>$</tt>
    * it denotes a attribute name and a value starting with <tt>_</tt>
    * references an other property.
    */
   public static PropertiesDocumentStructure xmlbsDTD = null;

   static {
      Properties prop = new Properties();
      try {
        String propertiesResource = "WordHtmlCleaner.properties";
        InputStream resourceAsStream = WordHtmlCleaner.class.getResourceAsStream(propertiesResource);
        if (resourceAsStream == null) {
            throw new RuntimeException("resource "+propertiesResource+" is not found");
        }
        prop.load(resourceAsStream);
        xmlbsDTD = new xmlbs.PropertiesDocumentStructure(prop);
        xmlbsDTD.setIgnoreCase(true);
      }
      catch (IOException e) {
        log.error("Unable to load word clean properties", e);
      }
   }

   public static String cleanXML(String textStr) {
      String xmlVersion = "";
      String docType = "";
      String htmlDoc = "";

      char[] data = textStr.toCharArray();

      for (int i = 0; i < data.length; i++) {
         if (data[i] == '<') {
            if (data[i + 1] == '?') {
               while (data[i] != '>') {
                  xmlVersion += data[i];
                  i++;
               }
               xmlVersion += data[i]; // nog even het afsluitende haakje
                                       // toevoegen
               continue;
            } else if (data[i + 1] == '!') {
               while (data[i] != '>') {
                  docType += data[i];
                  i++;
               }
               docType += data[i]; // nog even het afsluitende haakje toevoegen
               continue;
            }
         }
         htmlDoc += data[i];
      }

      return xmlVersion + docType + "<richtext>" + cleanHtml(htmlDoc)
            + "</richtext>";
   }

   /**
    * Cleans html code
    * 
    * @param textStr
    *           ugly html code
    * @return clean html code
    */
   public static String cleanHtml(String textStr) {
      log.debug("old value : " + textStr);
      if (textStr != null) {
         try {
            // The font tag is required to fix wordpad anchor links
            // xmlbs removes the fonttags

            String xmlStr = fixWordpad(textStr);
            xmlStr = fixLists(xmlStr);
            xmlStr = fixBR(xmlStr);
            xmlStr = replaceParagraph(xmlStr);
            xmlStr = removeXmlNamespace(xmlStr);
            xmlStr = removeEmptyTags(xmlStr);
            xmlStr = fixEmptyAnchors(xmlStr);
            xmlStr = fixAnchors(xmlStr);

            try {
               xmlbs.XMLBS xmlbs = new xmlbs.XMLBS("<body>" + xmlStr
                     + "</body>", xmlbsDTD);
               xmlbs.setRemoveEmptyTags(true);
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
            } catch (Throwable t) {
               log.error(Logging.stackTrace(t));
            }
            xmlStr = fixEmptyAnchors(xmlStr);
            xmlStr = shrinkBR(xmlStr);
            log.debug("new value : " + xmlStr);
            return xmlStr;
         } catch (IllegalStateException e) {
            log.error("Clean html failed");
            log.error(Logging.stackTrace(e));
         }
      }
      return "";
   }

   /**
    * remove xml namespace declarations
    * 
    * @param text
    *           xml string
    * @return xml string with namespace removed
    */
   private static String removeXmlNamespace(String text) {
      text = text.replaceAll("<.xml:namespace.*?/>", "");
      text = text.replaceAll("\\&lt;.xml:namespace.*?/\\&gt;", "");
      text = text.replaceAll("<.XML:NAMESPACE.*?/>", "");
      text = text.replaceAll("\\&lt;.XML:NAMESPACE.*?/\\&gt;", "");
      return text;
   }

   private static String replaceParagraph(String text) {
      // remove all remaining <p>
      text = text.replaceAll("<\\s*[pP]{1}\\s*.*?>", "");
      // replace all remaining </p> with a <br><br>
      text = text.replaceAll("<\\s*/[pP]{1}\\s*.*?>", "<br /><br />");
      // remove all <br> at the end
      text = text.replaceAll("(<\\s*[bB][rR]\\s*/?>|\\s|&nbsp;)+\\z", "");
      return text;
   }

    private static String shrinkBR(String text) {
        // remove all br's which are caused by more than one empty p
          text = text.replaceAll("(<[bB][rR]\\s*/?>\\s*){4,}", "<br /><br />");
        return text;
    }

   /**
    * Fixes the anchors tags for Wordpad: <U><FONT color=#0000ff> ... </U></FONT>
    * 
    * @param xmlStr
    *           xml string
    * @return xml string with fixed anchors
    */
   private static String fixWordpad(String xmlStr) {
      String xml = "";
      int begin = 0;
      int end = 0;
      while ((begin = nextResult(xmlStr, "<U><FONT color=#0000ff>", end)) > -1) {
         xml += xmlStr.substring(end, begin);
         end = nextResult(xmlStr, "</U></FONT>", begin);
         if (end > -1) {
            String link = xmlStr.substring(begin
                  + "<U><FONT color=#0000ff>".length(), end);
            xml += "<a href=\"" + stripHtml(link) + "\">" + link + "</a>";
            end += "</U></FONT>".length();
         } else {
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
      while ((begin = nextResult(xmlStr, "<li>", end)) > -1) {
         if (begin != end) {
            xml += xmlStr.substring(end, begin);
         }

         end = nextResult(xmlStr, "</li>", begin);
         if (end > -1) {
            end += "</li>".length();
            xml += xmlStr.substring(begin, end);
         } else {
            end = nextResult(xmlStr, "<li>", begin + "<li>".length());
            if (end == -1) {
               end = xmlStr.length();
            }

            int endList = nextResult(xmlStr, "</ol>", begin);
            if (endList == -1) {
               endList = nextResult(xmlStr, "</ul>", begin);
               if (endList == -1) {
                  endList = xmlStr.length();
               }
            }

            if (end <= endList) {
               xml += xmlStr.substring(begin, end) + "</li>";
               end -= 1;
            } else {
               if (end > endList) {
                  xml += xmlStr.substring(begin, endList) + "</li>";
                  end = endList;
                  if (endList != xmlStr.length()) {
                     xml += xmlStr.substring(endList, (endList + "</ol>"
                           .length()));
                     end += "</ol>".length();
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

   /**
    * Fixes the anchors tags puts the href in the body if the
    * 
    * @param xmlStr
    *           xml string
    * @return xml string with fixed anchors
    */
   private static String fixAnchors(String xmlStr) {
      String xml = "";
      int begin = 0;
      int end = 0;
      while ((begin = nextResult(xmlStr, "<a ", end)) > -1) {
         xml += xmlStr.substring(end, begin);
         int endBegin = xmlStr.indexOf(">", begin);
         end = nextResult(xmlStr, "</a>", begin);
         if (end > -1
               && "".equals(stripHtmlFromBody(xmlStr.substring(endBegin + 1, end)))) {
            String atag = xmlStr.substring(begin, endBegin + 1);
            int hrefBegin = nextResult(atag, "href=\"", 0);
            int nameBegin = nextResult(atag, "name=\"", 0);
            if (hrefBegin > -1) {
               hrefBegin += "href=\"".length();
               int hrefEnd = atag.indexOf("\"", hrefBegin);
               xml += atag + atag.substring(hrefBegin, hrefEnd) + "</a>";
            } else if (nameBegin > -1) {
               xml += atag + "</a>";
            }

            end += "</a>".length();
         } else {
            end += "</a>".length();
            xml += xmlStr.substring(begin, end);
         }
      }
      if (end < xmlStr.length()) {
         xml += xmlStr.substring(end);
      }
      return xml;
   }

   public static String fixEmptyAnchors(String xmlStr) {
      String xml = "";
      int begin = 0;
      int end = 0;
      while ((begin = nextResult(xmlStr, "<a ", end)) > -1) {
         xml += xmlStr.substring(end, begin);

         int gt = xmlStr.indexOf(">", begin);
         int closinggt = xmlStr.indexOf("/>", begin);
         boolean emptyTag = closinggt != -1 && gt >= closinggt + 1;
         if (emptyTag) {
            end = closinggt;
            xml += xmlStr.substring(begin, end) + "></a>";
            end += 2;
         } else {
            end = gt + 1;
            xml += xmlStr.substring(begin, end);
         }
      }
      if (end < xmlStr.length()) {
         xml += xmlStr.substring(end);
      }
      return xml;
   }

   private static int nextResult(String xmlStr, String substr, int from) {
      String upXmlStr = xmlStr.toLowerCase();
      String upSubstr = substr.toLowerCase();

      xmlStr.indexOf(upSubstr, from);
      
      return upXmlStr.indexOf(upSubstr, from);
   }

   private static String removeEmptyTags(String text) {
      return text.replaceAll("<[bBiIuU]\\s*/>", "");
   }

   private static String stripHtml(String text) {
      return text.replaceAll("<.+?>", "");
   }

   private static String stripHtmlFromBody(String text) {
       return text.replaceAll("<(?!img\\s|IMG\\s).+?>", "");
   }
   
   private static String fixBR(String text) {
      return text.replaceAll("<BR>", "<BR/>");
   }

}