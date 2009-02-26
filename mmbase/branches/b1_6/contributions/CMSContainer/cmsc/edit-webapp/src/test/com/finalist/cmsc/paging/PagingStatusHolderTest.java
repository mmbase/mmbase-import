package com.finalist.cmsc.paging;

import junit.framework.TestCase;

public class PagingStatusHolderTest extends TestCase {
   public void testGetSortToken(){
      PagingStatusHolder holder = new PagingStatusHolder();
      assertEquals("",holder.getSortToken());

      holder.setSort("mark.happy");
      holder.setDir("asc");
      assertEquals(" order by mark.happy asc",holder.getSortToken());

      holder.setSort("mark.happy,zig.sad");
      holder.setDir("asc");
      assertEquals(" order by mark.happy asc , zig.sad asc",holder.getSortToken());      
   }
}
