package com.finalist.cmsc.taglib.editors;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.mmbase.PropertiesUtil;

public class SetPropertyTag extends SimpleTagSupport {

   public String key;
   private String value;


   public void setKey(String key) {
      this.key = key;
   }


   public void setValue(String value) {
      this.value = value;
   }


   @Override
   public void doTag() {
      PropertiesUtil.setProperty(key, value);
   }

}
