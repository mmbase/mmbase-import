package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public final class GuiStepInfo {

   private GuiDescription description;
   private List<GuiImage> images = new ArrayList<GuiImage>();
   public String subtitle;
   public String title;

   public void addImage(GuiImage image) {
      images.add(image);
   }

   public GuiDescription getDescription() {
      return description;
   }

   public List<GuiImage> getImages() {
      return images;
   }

   public String getSubtitle() {
      return subtitle;
   }

   public String getTitle() {
      return title;
   }

   public void render(Element root) {
      Element stepinfo = toXml(root);
      stepinfo.setAttribute("title", title);
      stepinfo.setAttribute("subtitle", subtitle);
      if (description != null) {
         description.render(stepinfo);
      }
      Element imagesElement = XmlUtil.createChild(stepinfo, "stepimages");
      for (GuiImage image : images) {
         image.render(imagesElement);
      }
   }


   public void setDescription(GuiDescription description) {
      this.description = description;
   }

   public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Element toXml(Element root) {
      return XmlUtil.createChild(root, "stepinfo");
   }
}
