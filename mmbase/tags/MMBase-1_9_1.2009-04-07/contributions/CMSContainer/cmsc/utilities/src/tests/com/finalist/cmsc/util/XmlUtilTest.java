/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.w3c.dom.Document;


public class XmlUtilTest extends TestCase {

   public List<String[]> entities = new ArrayList<String[]>();
   public static final String ROOT_OPEN = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<root>";
   public static final String ROOT_CLOSE = "</root>";


   @Override
   protected void setUp() throws Exception {

      // XML entities
      entities.add(new String[] {"&lt;", "&lt;" , "&lt;"});
      entities.add(new String[] {"&gt;", "&gt;", "&gt;"});
      entities.add(new String[] {"&quot;", "&amp;quot;", "&quot;"});
      entities.add(new String[] {"&#123;", "&amp;#123;", "&#123;"});

      entities.add(new String[] {"&", "&amp;", "&"});
      entities.add(new String[] {"&amp;", "&amp;amp;", "&amp;"});
      entities.add(new String[] {"&amp;amp;", "&amp;amp;amp;", "&amp;amp;"});

      // HTML entities
      entities.add(new String[] {"&nbsp;", "&amp;nbsp;", "&nbsp;"});
      entities.add(new String[] {"&apos;", "&amp;apos;", "&apos;"});
      entities.add(new String[] {"&euro;", "&amp;euro;", "&euro;"});
      entities.add(new String[] {"&Aacute;", "&amp;Aacute;", "&Aacute;"});
   }

   public void testEscapedEntities() {
      for (String[] entry : entities) {
         String begin = entry[0];
         String escapedExpected = entry[1];
         String expected = entry[2];

         String escaped = XmlUtil.escapeXMLEntities(begin);
         assertEquals(escapedExpected, escaped);

         String actual = XmlUtil.unescapeXMLEntities(escaped);
         assertEquals(expected, actual);
      }
   }

   public void testSerializedEntities() {
      for (String[] entry : entities) {
         String xmlTags = "<test attr=\"%replace%\">%replace%</test>";

         String begin = xmlTags.replaceAll("%replace%", entry[1]);
         String expected = xmlTags.replaceAll("%replace%", entry[1]);

         String in = ROOT_OPEN + begin + ROOT_CLOSE;
         Document doc = XmlUtil.toDocument(in, false);

         String actual = XmlUtil.serializeDocument(doc, false, false, true, true);
         actual = actual.replaceAll("<.?root.?>", "");

         assertEquals(expected, actual);
      }
   }

   public void testEscapedAndSerializedEntities() {
      for (String[] entry : entities) {
         String xmlTags = "<test attr=\"%replace%\">%replace%</test>";

         String begin = xmlTags.replaceAll("%replace%", entry[0]);
         String expected = xmlTags.replaceAll("%replace%", entry[2]);

         // @see com.finalist.cmsc.richtext.Richtext#getRichTextDocument()
         String in = ROOT_OPEN + begin + ROOT_CLOSE;
         String escaped = XmlUtil.escapeXMLEntities(in);
         Document doc = XmlUtil.toDocument(escaped, false);

         // @see com.finalist.cmsc.richtext.Richtext#getRichTextString()
         String out = XmlUtil.serializeDocument(doc, false, false, true, true);
         out = out.replaceAll("<.?root.?>", "");
         String actual = XmlUtil.unescapeXMLEntities(out);

         assertEquals(expected, actual);
      }
   }
}
