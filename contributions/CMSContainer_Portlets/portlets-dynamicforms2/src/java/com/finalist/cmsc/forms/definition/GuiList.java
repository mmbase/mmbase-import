package com.finalist.cmsc.forms.definition;

import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.forms.value.ValueObject;
import com.finalist.cmsc.forms.value.ValuePathUtil;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiList {

   private String datapath;
   private String guitype;
   private GuiItem item;
   private int maxOccurence;
   private int minOccurence;

   public void addItem(GuiItem item) {
      this.item = item;
   }

   public void postLoad(DataObject data) {
      DataObject dataObject;
      if (datapath != null && !"".equals(datapath)) {
         dataObject = DataObject.getObjectFromPath(data, datapath);
      }
      else {
         dataObject = data;
      }
      item.postLoad(dataObject);
   }

   public String createDataObjects(ValueObject object) {
      if (datapath != null && !"".equals(datapath)) {
         return ValuePathUtil.createPath(object, datapath);
      }
      return "";
   }

   public String getDatapath() {
      return datapath;
   }

   public String getGuitype() {
      return guitype;
   }


   public int getMaxOccurence() {
      return maxOccurence;
   }


   public int getMinOccurence() {
      return minOccurence;
   }

   public boolean isValid(ValueObject object) {
      // int itemSize = object.getSize(datapath);
      ValueObject valueObject;
      boolean valid = true;

      List<ValueObject> objectList = ValuePathUtil.getListFromPath(object, datapath);

      int itemSize = 0;
      if (objectList != null) itemSize = objectList.size();

      for (int i = 0; i < itemSize; i++) {
         if (datapath != null && !"".equals(datapath)) {
            valueObject = objectList.get(i);
         }
         else {
            valueObject = object;
         }
         if (!item.isValid(valueObject)) {
            valid = false;
         }

      }
      return valid;
   }

   public void render(Element root, ValueObject object, String namePath, boolean rendercompleted) {
      Element list = toXml(root);
      ValueObject valueObject;

      List<ValueObject> objectList = ValuePathUtil.getListFromPath(object, datapath);
      int itemSize = 0;
      if (objectList != null) itemSize = objectList.size();

      for (int i = 0; i < itemSize; i++) {
         valueObject = objectList.get(i);
         String listpath = datapath.substring(datapath.indexOf("/") + 1, datapath.length());
         String itemPath = ValuePathUtil.createNamePath(namePath, listpath, i);
         item.render(list, valueObject, itemPath, rendercompleted);
      }
   }

   public void setDatapath(String datapath) {
      this.datapath = datapath;
   }

   public void setGuitype(String guitype) {
      this.guitype = guitype;
   }

   public void setMaxOccurence(int maxOccurence) {
      this.maxOccurence = maxOccurence;
   }

   public void setMinOccurence(int minOccurence) {
      this.minOccurence = minOccurence;
   }

   public Element toXml(Element root) {
      Element list = XmlUtil.createChild(root, "list");
      XmlUtil.createAttribute(list, "datapath", datapath);
      XmlUtil.createAttribute(list, "guitype", guitype);
      XmlUtil.createAttribute(list, "minoccurrence", minOccurence);
      return list;
   }
}
