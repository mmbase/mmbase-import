/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

/**
 * List the available Navigation items
 */
public class ListNavigationItemsTag extends AbstractListTag<NavigationItem> {

   private static final String MODE_HIDDEN = "hidden";
   private static final String MODE_MENU = "menu";
   private static final String MODE_ALL = "all";

   private String mode = MODE_MENU;
   private String type;

   @Override
   protected List<NavigationItem> getList() {
      List<NavigationItem> items = null;
      if (origin != null) {
         if (origin instanceof NavigationItem) {
            List<? extends NavigationItem> navigationItems = 
                SiteManagement.getNavigationItems((NavigationItem) origin, getChildNavigationClass());
            items = new ArrayList<NavigationItem>(navigationItems);
         }
      }
      else {
         items = new ArrayList<NavigationItem>(SiteManagement.getSites());
      }
      if (items != null) {
         if (!MODE_ALL.equalsIgnoreCase(mode)) {
             if (MODE_MENU.equalsIgnoreCase(mode)) {
                for (Iterator<? extends NavigationItem> iter = items.iterator(); iter.hasNext();) {
                   NavigationItem item = iter.next();
                   if (!item.isInmenu()) {
                      iter.remove();
                   }
                }
             }
             if (MODE_HIDDEN.equalsIgnoreCase(mode)) {
                for (Iterator<? extends NavigationItem> iter = items.iterator(); iter.hasNext();) {
                   NavigationItem item = iter.next();
                   if (item.isInmenu()) {
                      iter.remove();
                   }
                }
             }
         }
      }
      return items;
   }

   protected Class<? extends NavigationItem> getChildNavigationClass() {
       if (!StringUtils.isBlank(type)) {
           NavigationItemManager navigationManager = NavigationManager.getNavigationManager(type);
           if (navigationManager != null) {
               return navigationManager.getItemClass();
           }
           throw new IllegalArgumentException("type '"+type+"' is not registered as navigation");
       }
       return NavigationItem.class;
   }

   public String getMode() {
      return mode;
   }

   public void setMode(String mode) {
      this.mode = mode;
   }
   
   public String getType() {
       return type;
   }
   
   
   public void setType(String type) {
       this.type = type;
   }

   @Override
   public void setOrigin(Object dest) {
      if (dest != null) {
         NavigationItem item = SiteManagement.convertToNavigationItem(dest);
         super.setOrigin(item);
      }
   }

}
