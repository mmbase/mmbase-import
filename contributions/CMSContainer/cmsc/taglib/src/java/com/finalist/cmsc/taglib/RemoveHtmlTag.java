package com.finalist.cmsc.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class RemoveHtmlTag extends SimpleTagSupport {

   private String html;
   private String var;
   private Integer maxlength;


   public void setHtml(String html) {
      this.html = html;
   }


   public void setVar(String var) {
      this.var = var;
   }


   public void setMaxlength(Integer maxlength) {
      this.maxlength = maxlength;
   }


   @Override
   public void doTag() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      html = removeHtml(maxlength, html);

      request.setAttribute(var, html);
   }


   private static String removeHtml(Integer maxlength, String html) {
      StringBuffer result = new StringBuffer();
      int startIndex = 0;
      boolean keepGoing = true;
      while (keepGoing && (maxlength == null || result.length() < maxlength)) {
         int endIndex = html.indexOf("<", startIndex);
         if (endIndex == -1) {
            endIndex = html.length();
         }

         result.append(html.substring(startIndex, endIndex));
         startIndex = html.indexOf(">", endIndex) + 1;
         if (startIndex == 0) {
            keepGoing = false;
         }
      }

      String resultHtml = result.toString();
      if (maxlength != null && resultHtml.length() > maxlength) {
         int cropIndex = resultHtml.lastIndexOf(".", maxlength);
         if (cropIndex == -1) {
            cropIndex = resultHtml.lastIndexOf(" ", maxlength);
         }
         if (cropIndex == -1) {
            cropIndex = maxlength;
         }
         resultHtml = resultHtml.substring(0, cropIndex) + " ...";
      }
      return resultHtml;
   }
}
