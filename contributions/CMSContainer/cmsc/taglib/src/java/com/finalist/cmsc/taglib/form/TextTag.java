/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.lang.StringUtils;

public class TextTag extends SimpleTagSupport {
   public String var;
   public String value;


   public void setVar(String var) {
      this.var = var;
   }


   public void setValue(String value) {
      this.value = value;
   }


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      String attValue = (String) request.getAttribute(var);
      String inputValue = "";
      if (StringUtils.isNotEmpty(attValue)) {
         inputValue = attValue;
      }
      else {
         if (StringUtils.isNotEmpty(value)) {
            inputValue = attValue;
         }
      }
      ctx.getOut().print(
            "<input type=\"text\" " + "name=\"" + var + "\" " + "value=\"" + StringEscapeUtils.escapeHtml(inputValue)
                  + "\" />");
   }

}
