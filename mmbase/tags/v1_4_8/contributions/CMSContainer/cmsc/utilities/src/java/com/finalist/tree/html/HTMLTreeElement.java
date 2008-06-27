/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree.html;

import com.finalist.tree.TreeElement;

public class HTMLTreeElement extends TreeElement {

   protected String style;


   public HTMLTreeElement(String icon, String id, String name, String fragment, String style, String link, String target) {
      super(icon, id, name, fragment, link, target);
      this.style = style;
   }


   public HTMLTreeElement(String icon, String id, String name, String fragment, String style, String link) {
      super(icon, id, name, fragment, link);
      this.style = style;
   }


   public HTMLTreeElement(String icon, String id, String name, String fragment, String style) {
      super(icon, id, name, fragment);
      this.style = style;
   }


   public HTMLTreeElement(String icon, String id, String name, String fragment) {
      super(icon, id, name, fragment);
   }


   /**
    * @param style
    *           The style to set.
    */
   public void setStyle(String style) {
      this.style = style;
   }


   public String render(String imageBase) {
      StringBuffer buffer = new StringBuffer();
      if (icon != null) {
         buffer.append("<img src='" + imageBase + icon + "' alt='' border='0' align='top' valign='top'/> ");
      }

      buffer.append("<span id=\"treespan_" + id + "\"");
      if (style != null) {
         buffer.append(" class=\"" + style + "\"");
      }
      buffer.append(">");
      if (link != null) {
         buffer.append("<a href=\"" + link + "\"");
         if (target != null) {
            buffer.append(" target=\"" + target + "\"");
         }
         buffer.append(">");
      }
      buffer.append(name);
      if (link != null) {
         buffer.append("</a>");
      }
      buffer.append("</span>");

      return buffer.toString() + renderOptions(imageBase);
   }


   protected String renderOptions(String imageBase) {
      StringBuffer buffer = new StringBuffer();
      for (int count = 0; count < options.size(); count++) {
         String optionString = ((HTMLTreeOption) options.get(count)).render(imageBase);
         buffer.append(optionString);
      }
      return buffer.toString();
   }

}
