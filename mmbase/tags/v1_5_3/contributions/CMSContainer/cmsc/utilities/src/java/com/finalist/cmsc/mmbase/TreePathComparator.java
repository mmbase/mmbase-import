/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.mmbase;

import java.util.Comparator;

public class TreePathComparator implements Comparator<String> {

   public int compare(String path1, String path2) {
      int level1 = TreeUtil.getLevel(path1);
      int level2 = TreeUtil.getLevel(path2);

      if (level1 == level2) {
         return path1.compareTo(path2);
      }
      else {
         return level2 - level1;
      }
   }
}