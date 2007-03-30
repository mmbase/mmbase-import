package com.finalist.cmsc.knownvisitor.ntlm.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import jcifs.smb.NtlmPasswordAuthentication;

public class GetVisitorTag extends SimpleTagSupport {

   private String var;

   public void doTag() throws JspException, IOException {

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      NtlmPasswordAuthentication ntlm = (NtlmPasswordAuthentication) request.getSession().getAttribute("NtlmHttpAuth");
      if(ntlm != null) {
         request.setAttribute(var, ntlm.getName());
      }
   }
   
   public void setVar(String var) {
      this.var = var;
   }
   
}
