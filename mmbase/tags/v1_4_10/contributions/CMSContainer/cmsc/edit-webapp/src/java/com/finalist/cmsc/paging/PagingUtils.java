package com.finalist.cmsc.paging;

import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.PageContext;

public class PagingUtils {
   public static String generatePageUrl(PageContext pagecontext) {

      Long currentPage = (Long) pagecontext.findAttribute("currentPage");
      Integer pagenumber = (Integer) pagecontext.findAttribute("count");
      String style = "page_list_navfalse";

      if (currentPage.intValue() == pagenumber) {
         style = "page_list_navtrue";
      }

      StringBuffer href = href(pagecontext, Integer.toString(pagenumber - 1));


      String link = "<a href=\"%s\" class=\"%s\">%s</a>";

      return String.format(link, href, style, pagenumber);
   }

   private static StringBuffer href(PageContext pagecontext, String pageNumber) {
      String status = (String) pagecontext.findAttribute("status");
      String orderby = (String) pagecontext.findAttribute("orderby");
      String extraparams = (String) pagecontext.findAttribute("extraparams");
      StringBuffer href = new StringBuffer("?fun=paging");
      if (StringUtils.isNotEmpty(status)) {
         href.append("&status=" + status);
      }

      if (StringUtils.isNotEmpty(orderby)) {
         href.append("&orderby=" + orderby);
      }

      if (StringUtils.isNotEmpty(extraparams)) {
         href.append(extraparams);
      }

      href.append("&offset=" + pageNumber);
      return href;
   }

   public static String nextPage(PageContext pagecontext){
      Long currentPage = (Long) pagecontext.findAttribute("currentPage");
      //the offset value is 1 lesser than current page.
      Long nextPage = currentPage;
      return href(pagecontext,nextPage.toString()).toString();
   }

    public static String previousPage(PageContext pagecontext){
      Long currentPage = (Long) pagecontext.findAttribute("currentPage");
       //the offset value is 1 lesser than current page.
      Long nextPage = currentPage - 2;
      return href(pagecontext,nextPage.toString()).toString();
   }
}
