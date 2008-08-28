/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree.ajax;

import net.sf.mmapps.commons.util.XmlUtil;

import org.w3c.dom.Element;

import com.finalist.tree.TreeOption;

public class AjaxTreeOption extends TreeOption {

   public AjaxTreeOption(String icon, String title, String link, boolean showLabel) {
      super(icon, title, link, showLabel);
   }


   public AjaxTreeOption(String icon, String title, String link, String target, boolean showLabel) {
      super(icon, title, link, target, showLabel);
   }


   public AjaxTreeOption(String icon, String title, String link, String target) {
      super(icon, title, link, target);
   }


   public AjaxTreeOption(String icon, String title, String link) {
      super(icon, title, link);
   }


   public void render(Element item, String imageBase) {
      Element option = XmlUtil.createChild(item, "option");
      XmlUtil.createAttribute(option, "text", title);
      XmlUtil.createAttribute(option, "action", link);
      XmlUtil.createAttribute(option, "target", target);
      XmlUtil.createAttribute(option, "icon", imageBase + icon);
   }

}
