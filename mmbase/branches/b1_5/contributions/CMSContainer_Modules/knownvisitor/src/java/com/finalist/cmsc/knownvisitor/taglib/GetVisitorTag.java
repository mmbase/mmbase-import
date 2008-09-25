package com.finalist.cmsc.knownvisitor.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.knownvisitor.KnownVisitorModule;
import com.finalist.cmsc.knownvisitor.Visitor;

public class GetVisitorTag extends SimpleTagSupport {

   private String var;


   public void doTag() {

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      Visitor visitor = KnownVisitorModule.getInstance().getVisitor(request);
      if (visitor != null) {
         request.setAttribute(var, visitor);
      }
   }


   public void setVar(String var) {
      this.var = var;
   }

}
