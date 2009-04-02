/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree.html;

import com.finalist.tree.TreeOption;

public class HTMLTreeOption extends TreeOption {

   public HTMLTreeOption(String icon, String title, String link, boolean showLabel) {
      super(icon, title, link, showLabel);
   }


   public HTMLTreeOption(String icon, String title, String link, String target, boolean showLabel) {
      super(icon, title, link, target, showLabel);
   }


   public HTMLTreeOption(String icon, String title, String link, String target) {
      super(icon, title, link, target);
   }


   public HTMLTreeOption(String icon, String title, String link) {
      super(icon, title, link);
   }


   public String render(String imageBase) {
      StringBuffer buffer = new StringBuffer();
      if (link.startsWith("javascript:")) {
         buffer.append(" <a class=\"option\" href=\"#\"");
         buffer.append(" onclick=\"return " + link.substring("javascript:".length()) + ";\"");
      }
      else {
         buffer.append(" <a class=\"option\" href=\"" + link + "\"");
         if (target != null) {
            buffer.append(" target=\"" + target + "\"");
         }
      }
      buffer.append(">");
      if (icon != null) {
         buffer.append("<img src=\"" + imageBase + icon + "\" border=\"0\" align=\"top\"");
         if (title != null) {
            buffer.append(" title=\"" + title + "\"");
            buffer.append(" alt=\"" + title + "\"");
         }
         buffer.append("/>");
         if (showLabel && title != null) {
            buffer.append(title);
         }
      }
      else {
         if (title != null) {
            buffer.append(title);
         }
      }
      buffer.append("</a>");
      return buffer.toString();
   }

}
