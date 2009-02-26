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

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.util.bundles.JstlUtil;

public class OptionTag extends SimpleTagSupport {

   private String value;
   private String name;
   private String message;


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();

      SelectTag container = (SelectTag) findAncestorWithClass(this, SelectTag.class);
      String selected = container.selected;
      ctx.getOut().print("<option value=\"" + value + "\"");
      if (value.equals(selected)) {
         ctx.getOut().print(" selected=\"selected\"");
      }
      ctx.getOut().print(">");
      if (StringUtils.isNotEmpty(name)) {
         ctx.getOut().print(name);
      }
      else {
         if (StringUtils.isNotEmpty(message)) {
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            ctx.getOut().print(JstlUtil.getMessage(request, message));
         }
         else {
            ctx.getOut().print(value);
         }
      }
      ctx.getOut().print("</option>");
   }


   public String getMessage() {
      return message;
   }


   public void setMessage(String message) {
      this.message = message;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public String getValue() {
      return value;
   }


   public void setValue(String value) {
      this.value = value;
   }

}
