package com.finalist.cmsc.forms.definition;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public final class GuiDescription {

   private String title;
   private String value;

   public String getTitle() {
      return title;
   }

   public String getValue() {
      return value;
   }

   public void render(Element root) {
      toXml(root);
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public Element toXml(Element root) {
      Element description = XmlUtil.createChildText(root, "description", value);
      XmlUtil.createAttribute(description, "title", title);
      return description;
   }

}
