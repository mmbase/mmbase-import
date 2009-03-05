/*
 * WIAB - Web-in-a-Box
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is Web-In-A-Box.
 */
package com.finalist.cmsc.richtext;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.wordfilter.WordHtmlCleaner;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;

import com.finalist.cmsc.util.XmlUtil;

/**
 * Class for storing constants for richtext handling classes.
 */
public class RichText {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(RichText.class.getName());

   public final static String RICHTEXT_ROOT_OPEN = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<richtext>";

   public final static String RICHTEXT_ROOT_CLOSE = "</richtext>";

   public final static String LINK_TAGNAME = "a";
   public final static String IMG_TAGNAME = "img";

   public final static String DESTINATION_ATTR = "destination";
   public final static String RELATIONID_ATTR = "relationID";

   public final static String SRC_ATTR = "src";
   public final static String HREF_ATTR = "href";

   public final static String SIZE_ATTR = "imgsize";
   public final static String LEGEND = "legend";

   public static final String TITLE_ATTR = "title";
   public static final String DESCRIPTION_ATTR = "description";
   public static final String ALT_ATTR = "alt";
   public static final String WIDTH_ATTR = "width";
   public static final String HEIGHT_ATTR = "height";

   // MMBase stuff
   public final static String RICHTEXT_TYPE = "cmscrichtext";
   public final static String INLINEREL_NM = "inlinerel";
   public final static String IMAGEINLINEREL_NM = "imageinlinerel";
   public static final String REFERID_FIELD = "referid";
   public static final String TITLE_FIELD = "title";


   public final static boolean hasRichtextItems(String in) {
      return (in.indexOf("<" + RichText.LINK_TAGNAME) > -1 || in.indexOf("<" + RichText.IMG_TAGNAME) > -1);
   }


   public final static String cleanRichText(String originalValue, boolean replaceHeaders, boolean replaceParagraphs) {
      // if string is null or empty, (re)set it's value to empty string
      String newValue = "";
      if (originalValue != null && !"".equals(originalValue.trim())) {
         // Edited value: clean.
         log.debug("before cleaning: " + originalValue);
         newValue = WordHtmlCleaner.cleanHtml(originalValue, replaceHeaders, replaceParagraphs);
         log.debug("after cleaning: " + newValue);
      }
      return newValue;
   }


   public final static String getRichTextString(Document doc) {
      // to string and strip root node, doctype and xmldeclaration
      String out = XmlUtil.serializeDocument(doc, false, false, true, true);
      out = out.replaceAll("<.?richtext.?>", "");
      out = XmlUtil.unescapeXMLEntities(out);
      return out;
   }


   public final static Document getRichTextDocument(String in) {
      String out = XmlUtil.escapeXMLEntities(in);
      out = RichText.RICHTEXT_ROOT_OPEN + out + RichText.RICHTEXT_ROOT_CLOSE;
      Document doc = XmlUtil.toDocument(out, false);
      return doc;
   }
   
   public final static Object stripLinkAndImage(Node sourceNode,Field field,Map<Integer, Integer> copiedNodes,List<Integer> channels) {
      DataType dataType = field.getDataType();
      while (StringUtils.isEmpty(dataType.getName())) {
         dataType = dataType.getOrigin();
      }
      if ("cmscrichtext".equals(dataType.getName())) {
         String fieldname = field.getName();
         String fieldValue = (String) sourceNode.getValueWithoutProcess(fieldname);
         log.debug("richtext field: " + fieldname.trim());
       //  htmlFields.add(fieldname);
         if (StringUtils.isNotEmpty(fieldValue)) {
            try {
               if (hasRichtextItems(fieldValue)) {
                  Document doc = getRichTextDocument(fieldValue);
                  RichTextGetProcessor richTextGetProcessor = new RichTextGetProcessor();
                  richTextGetProcessor.resolve(sourceNode,doc,copiedNodes,channels);
                  String out = getRichTextString(doc);
                  out = WordHtmlCleaner.fixEmptyAnchors(out);
                  log.debug("final richtext text = " + out);
                  return out;
               }
            }
            catch (Exception e) {
               log.error("An error occured while resolving inline resources!", e);
            }
         }
      }
      return sourceNode.getValueWithoutProcess(field.getName());
   }

}
