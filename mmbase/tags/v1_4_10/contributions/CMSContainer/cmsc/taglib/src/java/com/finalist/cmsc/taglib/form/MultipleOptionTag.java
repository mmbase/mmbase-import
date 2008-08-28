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

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.util.bundles.JstlUtil;

public class MultipleOptionTag extends SimpleTagSupport {

   private String value;
   private String name;
   private String message;


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();

      MultipleSelectTag container = (MultipleSelectTag) findAncestorWithClass(this, MultipleSelectTag.class);
      boolean isSelected = container.isSelected(value);

      ctx.getOut().print("<option value=\"" + value + "\"");
      if (isSelected == true) {
         ctx.getOut().print(" selected=\"selected\"");
      }
      ctx.getOut().print(">");
      if (!StringUtil.isEmpty(name)) {
         ctx.getOut().print(name);
      }
      else {
         if (!StringUtil.isEmpty(message)) {
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
