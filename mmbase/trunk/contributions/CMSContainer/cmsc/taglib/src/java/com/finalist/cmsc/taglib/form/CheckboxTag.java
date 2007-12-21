/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class CheckboxTag extends SimpleTagSupport {

   public String var;
   public String value;
   public Object selected;

   @Override
   public void doTag() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();

      ctx.getOut().print("<input type=\"checkbox\" class=\"checkbox\" name=\"" + var + "\" value=\"" + value + "\" ");
      if (isSelected(ctx.getRequest()) == true) {
         ctx.getOut().print("checked=\"checked\"");
      }
      ctx.getOut().print(">");
      JspFragment frag = getJspBody();
      if (frag != null) {
         frag.invoke(null);
      }
   }

   private boolean isSelected(ServletRequest request) {
      Object selectedValues = request.getAttribute(var);
      if (selectedValues != null) {
         if (selectedValues instanceof String) {
            return ((String) selectedValues).equals(value);
         } else if (selectedValues instanceof String[]) {
            String[] selected = (String[]) selectedValues;
            List<String> selectedItems = Arrays.asList(selected);
            if (selectedItems.contains(value)) {
               return true;
            }
         } else if (selectedValues instanceof List) {
            List<String> selectedItems = (ArrayList<String>) selectedValues;
            if (selectedItems.contains(value)) {
               return true;
            }
         }
      }
      return false;
   }

   public void setVar(String var) {
      this.var = var;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setSelected(Object selected) {
      this.selected = selected;
   }

}