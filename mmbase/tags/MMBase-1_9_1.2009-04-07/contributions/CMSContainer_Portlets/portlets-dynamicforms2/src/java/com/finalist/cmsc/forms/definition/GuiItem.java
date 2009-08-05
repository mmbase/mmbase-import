package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.forms.value.ValueObject;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiItem {

   private List<GuiList> lists = new ArrayList<GuiList>();
   private List<GuiSection> sections = new ArrayList<GuiSection>();

   public void addList(GuiList list) {
      lists.add(list);
   }

   public void addSection(GuiSection section) {
      sections.add(section);
   }

   public void postLoad(DataObject dataObject) {
      for (GuiSection section : sections) {
         section.postLoad(dataObject);
      }
      for (GuiList list : lists) {
         list.postLoad(dataObject);
      }
   }

   public boolean isValid(ValueObject object) {
      boolean valid = true;
      for (GuiSection section : sections) {
         if (!section.isValid(object)) {
            valid = false;
         }
      }
      for (GuiList list : lists) {
         if (!list.isValid(object)) {
            valid = false;
         }
      }
      return valid;
   }

   public void render(Element root, ValueObject object, String namePath, boolean rendercompleted) {
      Element item = toXml(root);
      for (GuiSection section : sections) {
         section.render(item, object, namePath, rendercompleted);
      }
      for (GuiList list : lists) {
         list.render(item, object, namePath, rendercompleted);
      }
   }

   public Element toXml(Element root) {
      Element item = XmlUtil.createChild(root, "item");
      return item;
   }

}
