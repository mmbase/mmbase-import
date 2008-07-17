/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.util.Iterator;
import java.util.List;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.AbstractListTag;

/**
 * path of items valid attributes for this tag are:
 * <ul>
 * <li>mode := <strong>menu</strong>|hidden|all</li>
 * <li>includeSite := <strong>true</strong>|false</li>
 * <li>item := 1..n</li>
 * </ul>
 * Examples: <cmsc:path var="listPath" /> <cmsc:path var="listPath" mode="all"
 * includeSite="false" page="${myPage}"/>
 */
public class PathTag extends AbstractListTag<NavigationItem> {

   private static final String MODE_HIDDEN = "hidden";
   private static final String MODE_MENU = "menu";

   private String mode = MODE_MENU;
   private boolean includeSite = true;
   private int itemNumber;


   public boolean isIncludeSite() {
      return includeSite;
   }


   /**
    * Controls whether the name of the site should be included as part of the
    * path.
    * 
    * @param includeSite
    *           true to include the name of the site (default), false otherwise
    */
   public void setIncludeSite(boolean includeSite) {
      this.includeSite = includeSite;
   }


   public String getMode() {
      return mode;
   }


   /**
    * Controls which part of path should be shown depending whether it is in the
    * menu or not. <br>
    * menu: show all up to the first one which is not part of the menu (default)
    * <br>
    * hidden: show all up to the first one which is in the menu <br>
    * all: show all
    * 
    * @param mode
    *           String literal menu (default), hidden or all
    */
   public void setMode(String mode) {
      this.mode = mode;
   }


   public int getPage() {
      return itemNumber;
   }


   /**
    * Specify the page for which the path should be constructed, if zero
    * (default) the current path is used
    * 
    * @param page
    *           positive integer indicating a page or zero (default) for the
    *           current page
    */
   public void setPage(int page) {
      this.itemNumber = page;
   }


   @Override
   protected List<NavigationItem> getList() {
      String path;

      if (itemNumber > 0) {
         // get path for a specific item
         path = getPathForItem();
      }
      else {
         // get path for current item
         path = getPath();
      }
      if (path == null) {
         return null;
      }

      List<NavigationItem> items = SiteManagement.getListFromPath(path);
      if (items == null) {
         return items;
      }

      if (MODE_MENU.equalsIgnoreCase(mode)) {
         boolean hideChildren = false;
         for (Iterator<NavigationItem> iter = items.iterator(); iter.hasNext();) {
            NavigationItem item = iter.next();
            if (hideChildren || !item.isInmenu()) {
               iter.remove();
               hideChildren = true;
            }
         }
      }
      else if (MODE_HIDDEN.equalsIgnoreCase(mode)) {
         boolean showChildren = false;
         for (Iterator<NavigationItem> iter = items.iterator(); iter.hasNext();) {
            NavigationItem item = iter.next();
            if (showChildren || item.isInmenu()) {
               iter.remove();
               showChildren = true;
            }
         }
      }

      // remove the first entry if the site itself should not be shown
      if (!includeSite && items.size() > 0) {
         items.remove(0);
      }

      return items;
   }


   private String getPathForItem() {
      String path = null;

      NavigationItem tmpItem = SiteManagement.getNavigationItem(itemNumber);
      if (tmpItem != null) {
         path = SiteManagement.getPath(tmpItem, true);
      }

      return path;
   }

}
