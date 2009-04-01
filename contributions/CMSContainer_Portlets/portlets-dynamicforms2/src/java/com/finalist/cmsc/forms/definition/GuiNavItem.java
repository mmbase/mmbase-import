package com.finalist.cmsc.forms.definition;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public class GuiNavItem {

   private String guiclass;
   private String guitype;
   private String imageurl;
   private String screen;
   private String step;
   private String title;

   public String getGuiclass() {
      return guiclass;
   }

   public String getGuitype() {
      return guitype;
   }

   public String getImageurl() {
      return imageurl;
   }

   public String getScreen() {
      return screen;
   }

   public String getStep() {
      return step;
   }

   public String getTitle() {
      return title;
   }

   public void render(Element root) {
      toXml(root);
   }

   public void setGuiclass(String guiclass) {
      this.guiclass = guiclass;
   }

   public void setGuitype(String guitype) {
      this.guitype = guitype;
   }

   public void setImageurl(String imageurl) {
      this.imageurl = imageurl;
   }

   public void setScreen(String screen) {
      this.screen = screen;
   }

   public void setStep(String step) {
      this.step = step;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   private Element toXml(Element root) {
      Element navitem = XmlUtil.createChild(root, "navitem");
      XmlUtil.createAttribute(navitem, "title", title);
      XmlUtil.createAttribute(navitem, "step", step);
      XmlUtil.createAttribute(navitem, "screen", screen);
      XmlUtil.createAttribute(navitem, "imageurl", imageurl);
      XmlUtil.createAttribute(navitem, "class", guiclass);
      XmlUtil.createAttribute(navitem, "guitype", guitype);
      return navitem;
   }
}
