package com.finalist.cmsc.taglib.editors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.mmbase.PropertiesUtil;

public class PropertyTag extends SimpleTagSupport {

   /**
    * JSP variable name.
    */
   public String var;

   private String key;


   public void setKey(String key) {
      this.key = key;
   }


   public void setVar(String var) {
      this.var = var;
   }


   public String getVar() {
      return var;
   }


   @Override
   public void doTag() throws IOException {
      String property = PropertiesUtil.getProperty(key);

      PageContext ctx = (PageContext) getJspContext();
      if (StringUtils.isNotEmpty(var)) {
         HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
         // put in variable
         if (property != null) {
            request.setAttribute(var, property);
         }
         else {
            request.removeAttribute(var);
         }
      }
      else {
         ctx.getOut().write(property);
      }
   }

}
