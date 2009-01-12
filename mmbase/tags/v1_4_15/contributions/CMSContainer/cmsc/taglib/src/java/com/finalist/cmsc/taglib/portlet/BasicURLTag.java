package com.finalist.cmsc.taglib.portlet;

import java.io.IOException;

import javax.portlet.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.VariableInfo;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

/**
 * Supporting class for the <CODE>actionURL</CODE> and <CODE>renderURL</CODE>
 * tag. Creates a url that points to the current Portlet and triggers an action
 * request with the supplied parameters.
 */
public abstract class BasicURLTag extends TagSupport {

   public static class TEI extends TagExtraInfo {
      @Override
      public VariableInfo[] getVariableInfo(TagData tagData) {
         VariableInfo vi[] = null;
         String var = tagData.getAttributeString("var");
         if (var != null) {
            vi = new VariableInfo[1];
            vi[0] = new VariableInfo(var, "java.lang.String", true, VariableInfo.AT_END);
         }
         return vi;
      }
   }

   protected String page;
   protected String window;
   protected String elementId;

   protected String portletMode;
   protected String secure;
   protected Boolean secureBoolean;
   protected String windowState;
   protected String var;

   protected PortletURL url;
   protected String contenturl;


   /*
    * (non-Javadoc)
    * 
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   @Override
   public int doStartTag() throws JspException {
      if (var != null) {
         pageContext.removeAttribute(var, PageContext.PAGE_SCOPE);
      }
      // make sure we start with an unset url variable;
      setUrl(null);

      if (StringUtil.isEmpty(page) && StringUtil.isEmpty(window) && !StringUtil.isEmpty(elementId)) {
         contenturl = ResourcesUtil.getServletPathWithAssociation("content", "/content/*", elementId, null);
         return EVAL_PAGE;
      }

      PortletURL renderUrl = getRenderUrl();
      if (renderUrl == null) {
         throw new JspException("RenderUrl is not found");
      }
      else {
         setUrl(renderUrl);
         if (portletMode != null) {
            try {
               PortletMode mode = new PortletMode(portletMode);
               url.setPortletMode(mode);
            }
            catch (PortletModeException e) {
               throw new JspException(e);
            }
         }
         if (windowState != null) {
            try {
               WindowState state = new WindowState(windowState);
               url.setWindowState(state);
            }
            catch (WindowStateException e) {
               throw new JspException(e);
            }
         }
         if (secure != null) {
            try {
               url.setSecure(getSecureBoolean());
            }
            catch (PortletSecurityException e) {
               throw new JspException(e);
            }
         }
         if (elementId != null) {
            url.setParameter("elementId", elementId);
         }
      }
      return EVAL_PAGE;
   }


   protected abstract PortletURL getRenderUrl();


   /**
    * @return int
    */
   @Override
   public int doEndTag() throws JspException {
      if (var == null) {
         try {
            JspWriter writer = pageContext.getOut();
            if (url == null) {
               writer.print(contenturl);
            }
            else {
               writer.print(url);
            }
         }
         catch (IOException ioe) {
            throw new JspException("actionURL/renderURL Tag Exception: cannot write to the output writer.", ioe);
         }
      }
      else {
         if (url == null) {
            pageContext.setAttribute(var, contenturl, PageContext.PAGE_SCOPE);
         }
         else {
            pageContext.setAttribute(var, url.toString(), PageContext.PAGE_SCOPE);
         }
      }
      page = null;
      window = null;
      elementId = null;

      portletMode = null;
      secure = null;
      secureBoolean = null;
      windowState = null;
      var = null;

      url = null;
      contenturl = null;

      return EVAL_PAGE;
   }


   public String getLink() {
      String link = "";
      NavigationItem item = SiteManagement.convertToNavigationItem(page);
      if (item != null) {
         link = SiteManagement.getPath(item, !ServerUtil.useServerName());
      }
      else {
         link = page;
      }

      return link;
   }

   public String getHost() {
      String host = null;
      if (ServerUtil.useServerName()) {
         NavigationItem item = SiteManagement.convertToNavigationItem(page);
         host = SiteManagement.getSite(item);
      }
      return host;
   }

   /**
    * Returns the portletMode.
    * 
    * @return String
    */
   public String getPortletMode() {
      return portletMode;
   }


   /**
    * @return secure as String
    */
   public String getSecure() {
      return secure;
   }


   /**
    * @return secure as Boolean
    */
   public boolean getSecureBoolean() {
      return this.secureBoolean.booleanValue();
   }


   /**
    * Returns the windowState.
    * 
    * @return String
    */
   public String getWindowState() {
      return windowState;
   }


   /**
    * @return PortletURL
    */
   public PortletURL getUrl() {
      return url;
   }


   /**
    * Returns the var.
    * 
    * @return String
    */
   public String getVar() {
      return var;
   }


   /**
    * Sets the portletMode.
    * 
    * @param portletMode
    *           The portletMode to set
    */
   public void setPortletMode(String portletMode) {
      this.portletMode = portletMode;
   }


   /**
    * Sets secure to boolean value of the string
    * 
    * @param secure
    */
   public void setSecure(String secure) {
      this.secure = secure;
      this.secureBoolean = new Boolean(secure);
   }


   /**
    * Sets the windowState.
    * 
    * @param windowState
    *           The windowState to set
    */
   public void setWindowState(String windowState) {
      this.windowState = windowState;
   }


   /**
    * Sets the url.
    * 
    * @param url
    *           The url to set
    */
   public void setUrl(PortletURL url) {
      this.url = url;
   }


   /**
    * Sets the var.
    * 
    * @param var
    *           The var to set
    */
   public void setVar(String var) {
      this.var = var;
   }


   public String getPage() {
      return page;
   }


   public void setPage(String page) {
      this.page = page;
   }


   public String getWindow() {
      return window;
   }


   public void setWindow(String window) {
      this.window = window;
   }


   public String getElementId() {
      return elementId;
   }


   public void setElementId(String elementId) {
      this.elementId = elementId;
   }
}
