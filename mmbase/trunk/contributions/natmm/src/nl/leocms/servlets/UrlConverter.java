/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.servlets;

import java.util.*;
import javax.servlet.http.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;

import org.apache.commons.lang.StringUtils;
import nl.leocms.util.RubriekHelper;
import nl.leocms.util.PaginaHelper;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.tools.HtmlCleaner;

import javax.servlet.ServletContext;
import org.mmbase.module.core.MMBaseContext;

import nl.leocms.applications.*;

/**
 * Utility class that contains the logic that converts a URL into
 * a technical URL.
 * Furthermore, it contains the logic (ignoreURL-method) that determines whether a URL
 * is illegible for conversion in the first place.
 *
 * @author Finalist IT Group / peter
 * @version $Id: UrlConverter.java,v 1.11 2007-07-09 13:39:42 ieozden Exp $
 */
public final class UrlConverter {
   // some constants.

   public final static String PAGE_EXTENSION = ".htm";
   public static char ITEM_DELIMITER = '~';
   public static String PAGE_PARAM = "p";
   public static String RUBRIEK_PARAM = "r";
   public static String ITEM_PARAM = "id";
   public static String SEPARATOR = "/";

   public static String URL_CACHE = "url_cache";


   private UrlConverter() {
   }

   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(UrlConverter.class.getName());

   public static UrlCache getCache() {
      MMBaseContext mc = new MMBaseContext();
      ServletContext application = mc.getServletContext();
      UrlCache cache = (UrlCache)application.getAttribute(URL_CACHE);
      if (cache==null) {
        cache = new UrlCache();
        application.setAttribute(URL_CACHE,cache);
      }
      return cache;
   }


   /**
    * Method converts the semantic URL's into usable url.
    * This is done by looking up the right rubriek in the MMBase Cloud
    * and building up a URL to the ROOT_TEMPLATE adding the current rubriek
    * as a request parameter.
    * In case no matching category could be found, null is returned
    *
    * @param request Request object containing the URL to be converted
    * @return String the converted URL or null, if URL could not be converted.
    */
   public static String convertUrl(HttpServletRequest request) {
      String url = request.getRequestURL().toString();
      String params = request.getQueryString();
      return convertUrl(url, params);
   }
   
   public static Date getDate(String itemName) {
      Date itemDate = null;
      try {
        int year = (new Integer(itemName.substring(0, 2))).intValue();
        if (year < 50) {
           year += 2000;
        }
        else {
           year += 1900;
        }
        int month = (new Integer(itemName.substring(2, 4))).intValue() - 1;
        int day_of_month = (new Integer(itemName.substring(4, 6))).intValue();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day_of_month);
        itemDate = cal.getTime();
      }
      catch (Exception e) {
        log.debug("itemName does not contain valid date string");
      }
      return itemDate;
   }


    /**
    * Method converts the semantic URL's into usable url.
    * This is done by looking up the right category in the MMBase Cloud
    * and building up a URL to the ROOT_TEMPLATE adding the current category
    * as a request parameter.
    * In case no matching category could be found, null is returned
    *
    * @param url containing the URL to be converted
    * @return String the converted URL or null, if URL could not be converted.
    */
   public static String convertUrl(String url, String params) {
      log.debug("converting url: " + url);

      Cloud cloud = CloudFactory.getCloud();
      UrlCache cache = getCache();

      String jspURL = cache.getJSPEntry(url);
      if (jspURL!=null) {
        log.debug("processed from cache: " + jspURL);
        StringBuffer sb = new StringBuffer(jspURL);
        if (params != null) {
          sb.append('&').append(params);
        }
        return sb.toString();
      }

      //split URL in an item part, a page part and rubriekenpad
      String itemName = StringUtils.substringAfterLast(url, SEPARATOR);
      itemName = StringUtils.substringBefore(itemName, PAGE_EXTENSION);

      Date itemDate = null;
      String pageName = "";
      String rubriekenPad = "";
       
      if(NatMMConfig.useCreationDateInURL) {
         // check if the itemName starts with a date
         itemDate = getDate(itemName);
         if(itemDate!=null) {
           itemName = itemName.substring(6);
         } else { 
           itemName = null;
         }
      } else {
         // check if the itemName starts with a ITEM_DELIMITER
         if(itemName.charAt(0)==ITEM_DELIMITER) {
           itemName = itemName.substring(1);
         } else { 
           itemDate = getDate(itemName); // for backwards compatibility reasons
           if(itemDate!=null) {
             itemName = itemName.substring(6);
           } else { 
             itemName = null;
           }
        } 
      }
      
      if(itemName==null) { // no itemName specified
         pageName = StringUtils.substringAfterLast(url,SEPARATOR);
         pageName = StringUtils.substringBefore(pageName,PAGE_EXTENSION);
         rubriekenPad = StringUtils.substringBeforeLast(url,SEPARATOR);
      } else { // itemName specified
         url = StringUtils.substringBeforeLast(url,SEPARATOR);
         pageName = StringUtils.substringAfterLast(url,SEPARATOR);
         rubriekenPad = StringUtils.substringBeforeLast(url,SEPARATOR);
      }

      log.debug("itemName = " + itemName);
      log.debug("itemDate = " + itemDate);
      log.debug("pageName = " + pageName);
      log.debug("rubriekenPad = " + rubriekenPad);

      RubriekHelper rh = new RubriekHelper(cloud);
      ArrayList nlRubriek = rh.getRubriekWithRubriekenUrlPath(rubriekenPad, null);
      if (nlRubriek == null ) {
         log.debug("No matching rubriek found, interception stopped");
         return null;
      }

      PaginaHelper pg = new PaginaHelper(CloudFactory.getCloud());
      Node rubriek = null;
      Node page = null;
      int iRubriek = -1;
      int i =0;
      while(i < nlRubriek.size() && page==null) { // get the first (= newest) rubriek to which a page with this name is connected
         rubriek = (Node) nlRubriek.get(i);
         iRubriek = rubriek.getIntValue("number");
         log.debug("rubriek: " + iRubriek + " - " + rubriek.getStringValue("naam"));
         page = pg.retrievePagina("" + iRubriek, pageName);
         i++;
      }

      if(page == null) {
         log.debug("No matching page found, interception stopped");
         return null;
      } else {
         log.debug("matching page : " + page.getIntValue("number"));
         int pPos = rubriekenPad.indexOf(rh.getUrlPathToRootString(rubriek,"").toString());
         if(pPos>0) {
            rubriekenPad = rubriekenPad.substring(0,pPos);
         } else {
            rubriekenPad += "/";
         }
      }
      log.debug("rubriekenPad = " + rubriekenPad);

      String pageNumber = page.getStringValue("number");

      StringBuffer forwardUrl = new StringBuffer("");
      forwardUrl.append(rubriekenPad);
      forwardUrl.append(pg.getPaginaTemplate(pageNumber,""));

      forwardUrl.append(RUBRIEK_PARAM);
      forwardUrl.append('=');
      forwardUrl.append(iRubriek);

      forwardUrl.append('&');

      forwardUrl.append(PAGE_PARAM);
      forwardUrl.append('=');
      forwardUrl.append(pageNumber);

      if(itemName!=null) {
         Node item = pg.getContentElementNode(page, itemName);
         String itemNumber = item.getStringValue("number");

         forwardUrl.append('&');

         forwardUrl.append(ITEM_PARAM);
         forwardUrl.append('=');
         forwardUrl.append(itemNumber);
      }

      cache.putJSPEntry(forwardUrl.toString(),url);
      if (params != null) { forwardUrl.append('&').append(params); }

      log.debug("forward url: " + forwardUrl);
      return forwardUrl.toString();

   }


   /**
    * Checks whether URL should be ignored or not.  Criteria are:
    * - ignore is url contains a dot unless it ends on a certain page extension
    *
    * @param uri that is to be tested
    * @return boolean if Url should be returned or not
    */
   public static boolean ignoreUrl(String uri) {
      boolean ignore = false;
      if (uri.indexOf(PAGE_EXTENSION) == -1) {
         ignore = true;
      }
      return ignore;
   }

   public static void main(String Args[]) {
      String testUrl = "/Wonen/wonen.page";
      System.out.println("Testing this URL :" + testUrl);
      System.out.println("..Ignored        :" + ignoreUrl(testUrl));
      if(!ignoreUrl(testUrl)) {
         System.out.println("..Converted into :" + convertUrl(testUrl, ""));
      }
   }

}
