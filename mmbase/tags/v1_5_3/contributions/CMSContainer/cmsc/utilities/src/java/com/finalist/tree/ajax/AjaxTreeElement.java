/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree.ajax;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;
import com.finalist.tree.TreeElement;

public class AjaxTreeElement extends TreeElement {

   public AjaxTreeElement(String icon, String id, String name, String fragment, String link, String target) {
      super(icon, id, name, fragment, link, target);
   }


   public AjaxTreeElement(String icon, String id, String name, String fragment, String link) {
      super(icon, id, name, fragment, link);
   }


   public AjaxTreeElement(String icon, String id, String name, String fragment) {
      super(icon, id, name, fragment);
   }


   public void render(Element element, String imgBaseUrl) {
      XmlUtil.createAttribute(element, "text", this.name);
      XmlUtil.createAttribute(element, "action", this.link);
      XmlUtil.createAttribute(element, "target", this.target);
      XmlUtil.createAttribute(element, "persistentId", this.id);
      XmlUtil.createAttribute(element, "fragment", this.fragment);

      if (icon != null) {
         XmlUtil.createAttribute(element, "icon", imgBaseUrl + icon);
         XmlUtil.createAttribute(element, "openIcon", imgBaseUrl + icon);
      }

      renderOptions(element, imgBaseUrl);
   }


   protected void renderOptions(Element element, String imageBase) {
      for (int count = 0; count < options.size(); count++) {
         ((AjaxTreeOption) options.get(count)).render(element, imageBase);
      }
   }

}
