/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

/**
 * List the available Pages
 * 
 * @author Wouter Heijke
 */
public class ListPagesTag extends AbstractListTag<Page> {

   private static final String MODE_ALL = "all";
   private static final String MODE_HIDDEN = "hidden";
   private static final String MODE_MENU = "menu";

   private String mode = MODE_MENU;


   @Override
   protected List<Page> getList() {
      List<Page> pages = null;
      if (origin != null) {
         if (origin instanceof Site) {
            pages = SiteManagement.getPages((Site) origin);
         }
         else if (origin instanceof Page) {
            pages = SiteManagement.getPages((Page) origin);
         }
      }
      else {
         pages = new ArrayList<Page>(SiteManagement.getSites());
      }
      if (pages != null) {
         if (MODE_MENU.equalsIgnoreCase(mode)) {
            for (Iterator<? extends Page> iter = pages.iterator(); iter.hasNext();) {
               Page page = iter.next();
               if (!page.isInmenu()) {
                  iter.remove();
               }
            }
         }
         if (MODE_HIDDEN.equalsIgnoreCase(mode)) {
            for (Iterator<? extends Page> iter = pages.iterator(); iter.hasNext();) {
               Page page = iter.next();
               if (page.isInmenu()) {
                  iter.remove();
               }
            }
         }
      }
      return pages;
   }


   public String getMode() {
      return mode;
   }


   public void setMode(String mode) {
      this.mode = mode;
   }

   @Override
   public void setOrigin(Object dest) {
      Page page = null;
      if (dest != null) {
         if (dest instanceof Page) {
            page = (Page) dest;
         }
         else if (dest instanceof Integer) {
            page = getPageInteger((Integer) dest);
         }
         else if (dest instanceof String) {
            page = getPageString((String) dest);
         }
         else {
            throw new IllegalArgumentException("only Page, integer or string allowed: " + dest.getClass());
         }
         super.setOrigin(page);
      }
   }
   
   /**
    * Set destination node number to navigate to.
    * 
    * @param n
    *           the node number
    */
   private Page getPageInteger(Integer n) {
      return SiteManagement.getPage(n.intValue());
   }


   /**
    * Set the destination node path to navigate to.
    * 
    * @param s
    *           comma, slash or space separated list of node numbers and/or
    *           aliases
    */
   private Page getPageString(String s) {
      Page temp = null;
      if (!StringUtils.isBlank(s)) {
         if (StringUtils.isNumeric(s)) {
            temp = SiteManagement.getPage(Integer.parseInt(s));
         }
         else {
            temp = SiteManagement.getPageFromPath(s);
         }
      }
      return temp;
   }

}
