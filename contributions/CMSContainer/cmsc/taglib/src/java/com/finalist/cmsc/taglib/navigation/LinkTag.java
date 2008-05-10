/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.search.PageInfo;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * A tag to make URLs to other locations in the website.
 * 
 * @author Wouter Heijke
 * @author R.W. van 't Veer
 */
public class LinkTag extends CmscTag {

   /**
    * JSP variable name.
    */
   public String var;

   private String element;
   private String window;
   private String urlfragment;
   private String portletdefinition;
   private boolean restrictToCurrentPage;

   /**
    * Parameters added by nested param tag
    */
   private Map<String, Object> params = new HashMap<String, Object>();
   private NavigationItem page;
   private NavigationItem defaultPage;


   @Override
   public void doTag() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      if (page == null) {
         if (StringUtils.isNotBlank(urlfragment)) {
            // Computing the window for a given urlfragment can be expensive and will always yield
            // the same result. Therefore the computed pageId is cached in the page context.
            String key = "linktag_urlfragment_" + urlfragment;
            Integer pageId = (Integer) ctx.getAttribute(key);
            if (pageId == null) {
               String path = getPath();
               page = getPageWithUrlFragement(path, urlfragment);

               pageId = page != null ? page.getId() : -1;
               ctx.setAttribute(key, pageId);
            }
            else if (pageId == -1) {
               page = null;
            }
            else {
               page = SiteManagement.getNavigationItem(pageId);
            }
         }
         else {
            if (StringUtils.isNotBlank(portletdefinition)) {
               // Computing the window and page for a given portletdefinition can be expensive and
               // will always yield the same result. Therefore the computed values are cached in the
               // page context.
               String key = "linktag_portlet_" + portletdefinition;
               String value = (String) ctx.getAttribute(key);
               if (value == null) {
                  // No value in the page context, so we have to compute it now
                  String path = getPath();
                  setPageAndWindowBasedOnPortletDefinition(path, portletdefinition);

                  value = page != null ? page.getId() + "_" + window : "";
                  ctx.setAttribute(key, value); // Cache the result in the page context
               }
               else if ("".equals(value.trim())) {
                  // An empty String indicates a value is computed, but no page and window was found  
                  page = null;
                  window = null;
               }
               else {
                  // Otherwise the value has format 'pageId_window'
                  int splitAt = value.indexOf("_");
                  int pageId = Integer.valueOf(value.substring(0, splitAt));

                  page = SiteManagement.getNavigationItem(pageId);
                  window = value.substring(splitAt + 1);
               }
            }
         }
      }

      if (page == null) {
         page = defaultPage;
      }

      if (page != null) {
         String newlink = null;

         String externalurl = null;
         if (page instanceof Page) {
             externalurl = ((Page) page).getExternalurl();
         }
         if (StringUtils.isNotBlank(externalurl)) {
            if (externalurl.indexOf("://") > -1) {
               newlink = externalurl;
            }
            else {
               NavigationItem internalPage = SiteManagement.getNavigationItemFromPath(externalurl);
               if (internalPage != null) {
                  String link = SiteManagement.getPath(internalPage, !ServerUtil.useServerName());
                  newlink = getItemUrl(request, link);
               }
            }
         }
         else {
            String link = SiteManagement.getPath(page, !ServerUtil.useServerName());
            if (link != null) {
               newlink = getItemUrl(request, link);
            }
         }
         // handle result
         if (var != null) {
            // put in variable
            if (newlink != null) {
               request.setAttribute(var, newlink);
            }
            else {
               request.removeAttribute(var);
            }
         }
         else {
            if (newlink != null) {
               // write
               ctx.getOut().print(newlink);
            }
         }
         page = null;
         defaultPage = null;
         params.clear();
      }
      else {
         // log.warn("NO PAGE");
      }
   }


   private void setPageAndWindowBasedOnPortletDefinition(String path, String portletdefinition) {
      List<Page> pages = SiteManagement.getPagesFromPath(path);
      int lastIndexOfPages = pages.size() - 1;

      if (restrictToCurrentPage) {
         if(lastIndexOfPages >= 0) {
            Page currentPage = pages.get(lastIndexOfPages);
            String portletPosition = getPortletPositionWithDefinition(currentPage, portletdefinition);
            if (portletPosition != null) {
               window = portletPosition;
               page = currentPage;
            }
         }
         return;
      }

      for (int i = lastIndexOfPages; i >= 0; i--) {
         Page currentPage = pages.get(i);
         String portletPosition = getPortletPositionWithDefinition(currentPage, portletdefinition);
         if (portletPosition != null) {
            window = portletPosition;
            page = currentPage;
            return;
         }
         else {
            List<Page> childPages = SiteManagement.getPages(currentPage);
            // already examined the child which is on the path;
            Page childPageOfPath = (i == lastIndexOfPages) ? null : pages.get(i + 1);
            childPages.remove(childPageOfPath);
            for (Page childPage : childPages) {
               String childPortletPosition = getPortletPositionWithDefinition(childPage, portletdefinition);
               if (childPortletPosition != null) {
                  window = childPortletPosition;
                  page = childPage;
                  return;
               }
            }
         }
      }
   }


   private String getPortletPositionWithDefinition(Page currentPage, String portletdefinition) {
      Map<String, Integer> portlets = currentPage.getPortletsWithNames();
      for (Map.Entry<String, Integer> portletEntry : portlets.entrySet()) {
         Portlet portlet = SiteManagement.getPortlet(portletEntry.getValue());
         if (portlet != null) {
            PortletDefinition definition = SiteManagement.getPortletDefinition(portlet.getDefinition());
            if (definition != null && definition.getDefinition().equalsIgnoreCase(portletdefinition)) {
               return portletEntry.getKey();
            }
         }
      }
      return null;
   }


   private NavigationItem getPageWithUrlFragement(String path, String urlfragment) {
      List<Page> pages = SiteManagement.getPagesFromPath(path);
      int lastIndexOfPages = pages.size() - 1;
      for (int i = lastIndexOfPages; i >= 0; i--) {
         Page currentPage = pages.get(i);
         if (currentPage.getUrlfragment().equalsIgnoreCase(urlfragment)) {
            return currentPage;
         }
         else {
            List<Page> childPages = SiteManagement.getPages(currentPage);
            // already examined the child which is on the path;
            Page childPageOfPath = (i == lastIndexOfPages) ? null : pages.get(i + 1);
            childPages.remove(childPageOfPath);
            for (Page childPage : childPages) {
               if (childPage.getUrlfragment().equalsIgnoreCase(urlfragment)) {
                  return childPage;
               }
            }
         }
      }
      return null;
   }


   private String getItemUrl(HttpServletRequest request, String link) throws JspException, IOException {
      String newlink = null;
      if (link != null) {
         // handle body, call any nested tags
         JspFragment frag = getJspBody();
         if (frag != null) {
            StringWriter buffer = new StringWriter();
            frag.invoke(buffer);
         }

         String host = null;
         if (ServerUtil.useServerName()) {
            host = SiteManagement.getSite(page);
         }
         PortalURL u = new PortalURL(host, request, link);

         if (page instanceof Page) {
             addPortletParametersToUrl(u);
         }

         newlink = u.toString();

         if (newlink != null && newlink.length() == 0) {
            newlink = "/";
         }
      }
      return newlink;
   }

    private void addPortletParametersToUrl(PortalURL u) {
        if (element != null) {
            int pageId = page.getId();
            PageInfo pageInfo = Search.getPortletInformation(pageId, element);
            if (pageInfo != null) {
               window = pageInfo.getWindowName(); 
               u.setRenderParameter(window, "elementId", new String[] { element });
               for (Map.Entry<String, String> urlParam : pageInfo.getUrlParameters().entrySet()) {
                   u.setRenderParameter(pageInfo.getWindowName(), urlParam.getKey(), new String[] { urlParam.getValue() } );
               }
            }
            else {
               if (window != null) {
                   u.setRenderParameter(window, "elementId", new String[] { element });
               }
            }
         }

         if (window != null) {
            for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
               u.setRenderParameter(window, paramEntry.getKey(), new String[] { paramEntry.getValue().toString() });
            }
         }
    }


   /**
    * Set destination to navigate to.
    * 
    * @param dest
    *           the destination node, list of nodes or comma or slash separated
    *           node numbers or aliases
    */
   public void setDest(Object dest) {
      if (dest != null) {
         page = SiteManagement.convertToNavigationItem(dest);
      }
   }


   public void setUrlfragment(String urlfragment) {
      this.urlfragment = urlfragment;
   }


   public void setPortletdefinition(String portletdefinition) {
      this.portletdefinition = portletdefinition;
   }
   
   public void setRestrictToCurrentPage(boolean restrictToCurrentPage) {
      this.restrictToCurrentPage = restrictToCurrentPage;
   }


   public void setWindow(String window) {
      this.window = window;
   }


   public void setDefaultpage(Object dest) {
      if (dest != null) {
         defaultPage = SiteManagement.convertToNavigationItem(dest);
      }
   }


   public void setElement(String element) {
      this.element = element;
   }


   public void setVar(String var) {
      this.var = var;
   }


   protected void addParam(String name, Object value) {
      if (name != null && name.length() > 0) {
         params.put(name, value);
      }
   }

}
