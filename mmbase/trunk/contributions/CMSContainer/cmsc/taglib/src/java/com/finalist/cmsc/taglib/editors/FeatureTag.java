package com.finalist.cmsc.taglib.editors;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.util.module.ModuleUtil;

/**
 * Check if a named CMSC feature is active or installed
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class FeatureTag extends SimpleTagSupport {

   /**
    * Name of feature
    */
   private String name;


   @Override
   public void doTag() throws JspException, IOException {
      boolean hasFeature = ModuleUtil.checkFeature(name);
      if (hasFeature) {
         JspFragment frag = getJspBody();
         if (frag != null) {
            frag.invoke(null);
         }
      }
   }


   public void setName(String name) {
      this.name = name;
   }

}
