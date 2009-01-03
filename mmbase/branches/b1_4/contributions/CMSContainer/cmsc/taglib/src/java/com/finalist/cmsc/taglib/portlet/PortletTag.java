/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.portlet;

import java.io.IOException;
import java.util.*;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortalContextProvider;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.SiteManagementAdmin;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.core.*;
import com.finalist.pluto.portalImpl.factory.FactoryAccess;

/**
 * Container tag for a Portlet
 * 
 * @author Wouter Heijke
 */
public class PortletTag extends SimpleTagSupport {

   private static Log log = LogFactory.getLog(PortletTag.class);

   private String infovar;

   private PortletFragment portletFragment;


   public String getInfovar() {
      return infovar;
   }


   public void setInfovar(String infovar) {
      this.infovar = infovar;
   }


   @Override
   public void doTag() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

      portletFragment = (PortletFragment) request.getAttribute(PortalConstants.FRAGMENT);
      if (portletFragment != null) {
         if (infovar != null) {
            PortletInfo portletInfo = getPortletInfo(request, response);
            request.setAttribute(infovar, portletInfo);
         }

         // handle body, call any nested tags
         JspFragment frag = getJspBody();
         if (frag != null) {
            frag.invoke(null);
         }
      }
      else {
         log.error("PortletFragment: NOT FOUND");
      }
   }


   protected PortletInfo getPortletInfo(HttpServletRequest request, HttpServletResponse response) {
      PortletWindow portletWindow = portletFragment.getPortletWindow();

      PortalEnvironment env = (PortalEnvironment) request.getAttribute(PortalEnvironment.REQUEST_PORTALENV);

      PortletDefinition portletDefinition = portletWindow.getPortletEntity().getPortletDefinition();
      LanguageSet languageSet = portletDefinition.getLanguageSet();
      Language lang = languageSet.get(request.getLocale());
      String title = lang != null ? lang.getTitle() : "no title available";

      // create a PortletInfo object. This is used to communicate with
      // the header and footer JSP pages for this portlet
      PortletInfo portletInfo = new PortletInfo();

      // set the title, so the header JSP page can use it when rendering
      portletInfo.setTitle(title);

      Portlet portlet = portletFragment.getPortlet();
      portletInfo.setId(portlet.getId());
      
      com.finalist.cmsc.beans.om.PortletDefinition definition = SiteManagement.getPortletDefinition(portlet.getDefinition());
      if (definition != null) {
         portletInfo.setDefinitionId(definition.getId());
         portletInfo.setDefinitionTitle(definition.getTitle());
      }
      com.finalist.cmsc.beans.om.View view = SiteManagement.getView(portlet.getView());
      if (view != null) {
         portletInfo.setViewId(view.getId());
         portletInfo.setViewTitle(view.getTitle());
      }
      String responseContentType = response.getContentType();
      int indexOf = responseContentType.indexOf(";");
      if (indexOf > -1) {
         responseContentType = responseContentType.substring(0, indexOf);
      }
      ContentType supported = portletDefinition.getContentTypeSet().get(responseContentType);
      PortalContextProvider portalContextProvider = FactoryAccess.getStaticProvider().getPortalContextProvider();

      // get the list of modes this Portlet supports
      if (supported != null && portalContextProvider != null) {
         boolean mayEditPage = true;
         String pageId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
         if (pageId != null) {
            NavigationItem item = SiteManagement.getNavigationItem(Integer.valueOf(pageId));
            mayEditPage = SiteManagementAdmin.mayEdit(item);
         }
         boolean mayEditPortlet = SiteManagementAdmin.mayEdit(portlet);

         // if portlet supports portlet modes
         Iterator<PortletMode> modes = supported.getPortletModes();
         while (modes.hasNext()) {
            PortletMode mode = modes.next();

            // check whether portal also supports portlet mode
            boolean portalSupport = false;
            Iterator<PortletMode> portalSupportedModes = portalContextProvider.getSupportedPortletModes().iterator();
            while (portalSupportedModes.hasNext()) {
               PortletMode portalSupportedMode = portalSupportedModes.next();
               if (mode.equals(portalSupportedMode)) {
                  portalSupport = true;
                  break;
               }
            }

            // create links for portlet modes in portlet header
            if (portalSupport) {
               env = (PortalEnvironment) request.getAttribute(PortalEnvironment.REQUEST_PORTALENV);
               PortalURL modeURL = env.getRequestedPortalURL();

               PortalControlParameter control = new PortalControlParameter(modeURL);
               PortletMode currentMode = control.getMode(portletWindow);
               control.setMode(portletWindow, mode);

               boolean isVisible = false;
               if (mode.equals(CmscPortletMode.DELETE)) {
                  // Turn the delete mode in an action url instead of a render
                  // url
                  // The PortletContainerImpl will handle this action.
                  control.setAction(portletWindow);
               }

               String modeType = null;
               if (CmscPortletMode.isAdminMode(mode)) {
                  modeType = "admin";
                  if (mayEditPage && CmscPortletMode.isViewMode(currentMode)) {
                     if (mayEditPortlet) {
                        isVisible = true;
                     }
                  }
               }
               if (CmscPortletMode.isEditMode(mode)) {
                  modeType = "edit";
                  if ((CmscPortletMode.isViewMode(currentMode) && !CmscPortletMode.isEditOnlyMode(mode))
                        || CmscPortletMode.isEditMode(currentMode)) {
                     isVisible = true;
                  }
               }
               if (CmscPortletMode.isViewMode(mode)) {
                  modeType = "view";
                  isVisible = true;
               }
               portletInfo.addPortletMode(mode, modeURL.toString(control, new Boolean(request.isSecure())), mode
                     .equals(currentMode), isVisible, modeType);
            }
         }

         // get the list of window states this Portlet supports
         Iterator<WindowState> states = portalContextProvider.getSupportedWindowStates().iterator();
         while (states.hasNext()) {
            WindowState state = states.next();
            env = (PortalEnvironment) request.getAttribute(PortalEnvironment.REQUEST_PORTALENV);
            PortalURL stateURL = env.getRequestedPortalURL();
            PortalControlParameter control = new PortalControlParameter(stateURL);
            WindowState currentState = control.getState(portletWindow);

            control.setState(portletWindow, state);
            portletInfo.addPortletWindowState(state.toString().substring(0, 3), stateURL.toString(control, new Boolean(
                  request.isSecure())), state.equals(currentState));
         }
      }
      return portletInfo;
   }


   protected PortletFragment getPortletFragment() {
      return portletFragment;
   }

   public static class PortletInfo {

      private int id;
      private String title;
      private int viewId;
      private String viewTitle;
      private int definitionId;
      private String definitionTitle;
      
      private PortletModeInfo currentMode;
      private List<PortletModeInfo> availablePortletModes = new ArrayList<PortletModeInfo>();
      private List<PortletModeInfo> visiblePortletModes = new ArrayList<PortletModeInfo>();
      private List<PortletWindowStateInfo> availablePortletWindowStates = new ArrayList<PortletWindowStateInfo>();


      public int getId() {
         return id;
      }


      public void setId(int id) {
         this.id = id;
      }


      public String getTitle() {
         return title;
      }


      public void setTitle(String string) {
         title = string;
      }


      public List<PortletModeInfo> getAvailablePortletModes() {
         return availablePortletModes;
      }


      public List<PortletModeInfo> getVisiblePortletModes() {
         return visiblePortletModes;
      }


      public void addPortletMode(PortletMode mode, String activationURL, boolean isCurrent, boolean isVisible,
            String type) {
         PortletModeInfo pmi = new PortletModeInfo(mode.toString(), activationURL, isCurrent, type);
         availablePortletModes.add(pmi);

         if (isVisible && !isCurrent) {
            visiblePortletModes.add(pmi);
         }
         if (isCurrent) {
            currentMode = pmi;
         }
      }


      public List<PortletWindowStateInfo> getAvailablePortletWindowStates() {
         return availablePortletWindowStates;
      }


      public void addPortletWindowState(String stateLabel, String activationURL, boolean isCurrent) {
         PortletWindowStateInfo pwsi = new PortletWindowStateInfo(stateLabel, activationURL, isCurrent);
         availablePortletWindowStates.add(pwsi);
      }


      public PortletModeInfo getCurrentMode() {
         return currentMode;
      }
      
      public int getViewId() {
         return viewId;
      }
      
      public void setViewId(int viewId) {
         this.viewId = viewId;
      }

      public String getViewTitle() {
         return viewTitle;
      }
      
      public void setViewTitle(String viewTitle) {
         this.viewTitle = viewTitle;
      }

      public int getDefinitionId() {
         return definitionId;
      }
      
      public void setDefinitionId(int definitionId) {
         this.definitionId = definitionId;
      }

      public String getDefinitionTitle() {
         return definitionTitle;
      }
      
      public void setDefinitionTitle(String definitionTitle) {
         this.definitionTitle = definitionTitle;
      }

   }

   public static class PortletWindowStateInfo implements Comparable<PortletWindowStateInfo> {

      private String label;

      private String url;

      private boolean isCurrent;


      /**
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(PortletWindowStateInfo compare) {
         return this.getLabel().compareTo(compare.getLabel());
      }


      public PortletWindowStateInfo(String stateLabel, String activationURL, boolean isCurrent) {
         this.label = stateLabel;
         this.url = activationURL;
         this.isCurrent = isCurrent;
      }


      public String getLabel() {
         return label;
      }


      public void setLabel(String string) {
         label = string;
      }


      public boolean isCurrent() {
         return isCurrent;
      }


      public String getUrl() {
         return url;
      }


      public void setCurrent(boolean b) {
         isCurrent = b;
      }


      public void setUrl(String string) {
         url = string;
      }


      @Override
      public String toString() {
         return getLabel();
      }

   }

   public static class PortletModeInfo implements Comparable<PortletModeInfo> {

      private String name;
      private String url;
      private String type;
      private boolean isCurrent;


      public PortletModeInfo(String name, String url, boolean isCurrent, String type) {
         this.name = name;
         this.url = url;
         this.isCurrent = isCurrent;
         this.type = type;
      }


      public boolean isCurrent() {
         return isCurrent;
      }


      public String getName() {
         return name;
      }


      public String getUrl() {
         return url;
      }


      public String getType() {
         return type;
      }


      public void setCurrent(boolean b) {
         isCurrent = b;
      }


      public void setName(String string) {
         name = string;
      }


      public void setUrl(String string) {
         url = string;
      }


      /**
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(PortletModeInfo compare) {
         return this.getName().compareTo(compare.getName());
      }


      @Override
      public String toString() {
         return getName();
      }
   }

}
