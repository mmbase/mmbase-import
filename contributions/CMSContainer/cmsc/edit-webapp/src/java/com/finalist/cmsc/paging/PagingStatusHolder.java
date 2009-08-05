package com.finalist.cmsc.paging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import org.apache.commons.lang.StringUtils;

public class PagingStatusHolder {
   private int page;
   private int pageCount;
   private int pageSize = -1;
   private int listSize;
   private String sort;
   private String dir = "asc";

   public int getPage() {
      return page;
   }

   public void setPage(int page) {
      this.page = page;
   }

   public int getPageCount() {

      if (pageCount > 0) {
         return pageCount;
      }

      if (listSize < getPageSize()) {
         pageCount = 1;
      }
      else {
         pageCount = listSize / getPageSize();

         if (0 != listSize % getPageSize()) {
            pageCount++;
         }
      }

      return pageCount;
   }

   public int getPageSize() {
      String defalutPagesize = PropertiesUtil.getProperty("repository.search.results.per.page");
      if (pageSize < 0 && StringUtils.isBlank(defalutPagesize)) {
         return 50;
      }
      else if (pageSize < 0) {
         return Integer.parseInt(defalutPagesize);
      }
      else {
         return this.pageSize;
      }
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public String getSort() {
      return sort;
   }

   public void setSort(String sort) {
      this.sort = sort;
   }

   public int getListSize() {
      return listSize;
   }

   public void setListSize(int listSize) {
      this.listSize = listSize;
   }

   public int getOffset() {
      return page * getPageSize();
   }

   public String getDir() {
      return dir;
   }

   public void setDir(String dir) {
      this.dir = dir;
   }

   public void setDefaultSort(String column, String direction) {
      if (StringUtils.isBlank(this.sort) && StringUtils.isNotBlank(column)) {
         this.setSort(column);

         String dir = StringUtils.isNotBlank(direction) ? direction : "asc";
         this.setDir(dir);
      }
   }

   public String getMMBaseDirection() {

      return "asc".equals(this.dir) ? "up" : "down";

   }

   public String getSortToken() {
      if (null == sort) {
         return "";
      }

      StringBuffer token = new StringBuffer("");

      if (sort.contains(",")) {
         String[] sorts = StringUtils.split(sort,",");

         token.append(String.format(" order by %s %s", sorts[0], dir));

         for (int i = 1; i < sorts.length; i++) {
            token.append(String.format(" , %s %s", sorts[i], dir));
         }
      }
      else {
         token.append(String.format(" order by %s %s", sort, dir));
      }

      return token.toString();
   }
}
