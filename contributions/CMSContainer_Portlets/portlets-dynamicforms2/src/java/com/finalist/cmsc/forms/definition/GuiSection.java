package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.forms.value.ValueObject;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiSection {

   private List<Object> fields = new ArrayList<Object>();
   private GuiNavigation navigation;
   private String title;

   public void addField(GuiField field) {
      fields.add(field);
   }

   public void addFieldset(GuiFieldset fieldset) {
      fields.add(fieldset);
   }

   public void postLoad(DataObject dataObject) {
      for (Object field : fields) {
         if (field instanceof GuiFieldset) {
            ((GuiFieldset) field).postLoad(dataObject);
         }
         if (field instanceof GuiField) {
            ((GuiField) field).postLoad(dataObject);
         }
      }
   }

   public String getTitle() {
      return title;
   }

   public boolean isValid(ValueObject object) {
      boolean valid = true;
      for (Object field : fields) {
         if (field instanceof GuiFieldset) {
            if (!((GuiFieldset) field).isValid(object)) {
               valid = false;
            }
         }
         if (field instanceof GuiField) {
            if (!((GuiField) field).isValid(object)) {
               valid = false;
            }
         }
      }
      return valid;
   }

   public void render(Element root, ValueObject object, String namePath, boolean rendercompleted) {
      Element section = toXml(root);
      for (Object field : fields) {
         if ((rendercompleted && object.isCompleted()) || !rendercompleted) {
            if (field instanceof GuiFieldset) {
               ((GuiFieldset) field).render(section, object, namePath);
            }
            if (field instanceof GuiField) {
               ((GuiField) field).render(section, object, namePath);
            }
         }
      }
      if (navigation != null) {
         navigation.render(section);
      }
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Element toXml(Element root) {
      Element section = XmlUtil.createChild(root, "section");
      XmlUtil.createAttribute(section, "title", title);
      return section;
   }
}
