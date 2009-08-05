package com.finalist.cmsc.forms.definition;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public final class GuiImage {

   private String status;
   private String titleUlr;

   public void render(Element root) {
      toXml(root);
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setTitleUlr(String titleUlr) {
      this.titleUlr = titleUlr;
   }

   public Element toXml(Element root) {
      Element image = XmlUtil.createChild(root, "image");
      XmlUtil.createAttribute(image, "steptitleurl", titleUlr);
      XmlUtil.createAttribute(image, "stepstatus", status);
      return image;
   }
}
