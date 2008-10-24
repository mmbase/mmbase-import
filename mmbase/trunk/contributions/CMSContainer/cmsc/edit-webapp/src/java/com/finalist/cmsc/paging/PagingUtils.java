package com.finalist.cmsc.paging;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class PagingUtils {

   private static ThreadLocal<PagingStatusHolder> pool = new ThreadLocal<PagingStatusHolder>();

   public static PagingStatusHolder getStatusHolder() {
      return pool.get();
   }

   public static final int DEFAULTPAGESIZE = 30;
   public static final int FIRSTPAGE = 1;

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
      String type = (String) pagecontext.findAttribute("type");
      String status = (String) pagecontext.findAttribute("status");
      String orderby = (String) pagecontext.findAttribute("orderby");
      String extraparams = (String) pagecontext.findAttribute("extraparams");
      StringBuffer href = new StringBuffer("?fun=paging");
      if (StringUtils.isNotEmpty(type)) {
         href.append("&type=" + type);
      }

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

   public static String nextPage(PageContext pagecontext) {
      Long currentPage = (Long) pagecontext.findAttribute("currentPage");
      // the offset value is 1 lesser than current page.
      Long nextPage = currentPage;
      return href(pagecontext, nextPage.toString()).toString();
   }

   public static String previousPage(PageContext pagecontext) {
      Long currentPage = (Long) pagecontext.findAttribute("currentPage");
      // the offset value is 1 lesser than current page.
      Long nextPage = currentPage - 2;
      return href(pagecontext, nextPage.toString()).toString();
   }

   public static int getSystemPageSize() {
      return 30;
   }

   /**
    * @param request
    * @return
    * @deprecated use initStatusHolder instead
    */
   public static PagingStatusHolder getStatusHolder(HttpServletRequest request) {
      PagingUtils.initStatusHolder(request);
      return getStatusHolder();
   }

   public static void initStatusHolder(HttpServletRequest request) {
      PagingStatusHolder holder = new PagingStatusHolder();
      pool.set(holder);

      String page = request.getParameter("page");
      String sort = request.getParameter("sortby");
      String dir = request.getParameter("dir");

      if (StringUtils.isNotBlank(page)) {
         holder.setPage(Integer.parseInt(page));
      } else {
         holder.setPage(0);
      }

      if (StringUtils.isNotBlank(sort)) {
         holder.setSort(sort);
      }

      if (StringUtils.isNotBlank(dir)) {
         holder.setDir(dir);
      }
   }

   public static PagingStatusHolder getStatusHolderInSorting(String column,
         String direction) {
      PagingStatusHolder holder = getStatusHolder();

      if (null != holder) {
         holder.setDefaultSort(column, direction);
      }

      return holder;
   }

   public static void setPagingAndSortingInformation(NodeQuery query) {
      PagingStatusHolder pagingHolder = getStatusHolder();
      query.setOffset(pagingHolder.getOffset());
      query.setMaxNumber(pagingHolder.getPageSize());
      Queries.addSortOrders(query, pagingHolder.getSort(), pagingHolder
            .getMMBaseDirection());
   }
}
