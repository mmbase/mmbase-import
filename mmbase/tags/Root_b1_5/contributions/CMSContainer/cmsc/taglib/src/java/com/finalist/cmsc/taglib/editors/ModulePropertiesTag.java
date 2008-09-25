package com.finalist.cmsc.taglib.editors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.mmbase.PropertiesUtil;

public class ModulePropertiesTag extends SimpleTagSupport {

   public String module;

   private String var;


   public void setModule(String module) {
      this.module = module;
   }


   public void setVar(String var) {
      this.var = var;
   }


   public String getVar() {
      return var;
   }


   @Override
   public void doTag() {
      Map<String, String> properties = PropertiesUtil.getModuleProperties(module);

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      request.setAttribute(var, properties);
   }

}
