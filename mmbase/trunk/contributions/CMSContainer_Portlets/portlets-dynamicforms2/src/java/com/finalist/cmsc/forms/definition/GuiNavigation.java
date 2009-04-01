package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public class GuiNavigation {

   private GuiDescription description;
   private List<GuiNavItem> navitems = new ArrayList<GuiNavItem>();

   public void addItem(GuiNavItem navItem) {
      navitems.add(navItem);
   }

   public void render(Element root) {
      Element navigation = toXml(root);
      if (description != null) {
         description.render(navigation);
      }
      for (GuiNavItem navitem : navitems) {
         navitem.render(navigation);
      }
   }

   public void setDescription(GuiDescription description) {
      this.description = description;
   }

   private Element toXml(Element root) {
      Element navigation = XmlUtil.createChild(root, "navigation");
      return navigation;
   }
}
