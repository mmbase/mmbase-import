/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import java.util.*;

import javax.servlet.http.*;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.security.Rank;
import org.mmbase.util.Encode;

public abstract class MMBaseAction extends Action {

   public static final String SUCCESS = "success";
   public static final String CANCEL = "cancel";

   public static final String ADMINISTRATOR = "administrator";
   public static final String SITEADMIN ="siteadmin";
   public static final String BASIC_USER = "basic user";
   public static final String ANONYMOUS = "anonymous";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {

      Cloud cloud = getCloud();
      if (cloud == null) {
         cloud = getCloudFromSession(request);
         if (cloud == null) {
            cloud = createCloud(request);
            if (cloud == null) {
               Rank requiredRank = getRequiredRank();
               if (requiredRank == null) {
                  try {
                     cloud = CloudProviderFactory.getCloudProvider().getCloud();
                  }
                  catch (BridgeException be) {
                     cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
                  }
               }
               else {
                  return redirectLogin(request, response);
               }
            }
         }
      }
      if (cloud == null) {
         throw new IllegalArgumentException("Unable to get a cloud from action, request, session and cloudprovider");
      }
      else {
         Rank requiredRank = getRequiredRank();
         if (requiredRank != null) {
            if (requiredRank.getInt() > cloud.getUser().getRank().getInt()) {
               return redirectLogin(request, response);
            }
         }
      }

      return execute(mapping, form, request, response, cloud);
   }


   public abstract ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception;


   protected Node getOrCreateNode(MMBaseForm form, Cloud cloud, String nodeManager) {
      Node node;
      int id = form.getId();
      if (id == -1) {
         node = cloud.getNodeManager(nodeManager).createNode();
      }
      else {
         node = cloud.getNode(id);
      }
      return node;
   }


   public Cloud getCloud() {
      return null;
   }


   public Rank getRequiredRank() {
      String rank = getRequiredRankStr();
      if (rank != null) {
         return Rank.getRank(rank);
      }
      return null;
   }


   public String getRequiredRankStr() {
      return BASIC_USER;
   }


   /**
    * @deprecated Use {@link #redirectLogin(HttpServletRequest, HttpServletResponse)} instead.
    */
   protected ActionForward redirectLogin(HttpServletRequest req) {
      return redirectLogin(req, null);
   }
   
   
   protected ActionForward redirectLogin(HttpServletRequest req, HttpServletResponse resp) {
      // could not create a cloud on the session
      String loginForward = "/editors/login.jsp";
      String referrer = req.getRequestURL().toString()
            + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
      loginForward += "?referrer=" + Encode.encode("ESCAPE_URL", referrer);
      loginForward += "&reason=failed";

      ActionForward ret = new ActionForward(loginForward);
      ret.setRedirect(true);
      return ret;
   }


   public String getParameter(HttpServletRequest request, String name) {
      return getParameter(request, name, null);
   }


   public String getParameter(HttpServletRequest request, String name, boolean required) {
      String value = getParameter(request, name, null);
      if (required && value == null) {
         throw new IllegalArgumentException("Parameter " + name + " is required, but not found");
      }
      return value;
   }


   public String getParameter(HttpServletRequest request, String name, String defaultValue) {
      String value = request.getParameter(name);
      if (value == null) {
         value = (String) request.getAttribute(name);
         if (value == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
               value = (String) session.getAttribute(name);
            }
         }
      }
      if (value == null) {
         value = defaultValue;
      }
      return value;
   }


   public static Cloud createCloud(HttpServletRequest req) {
      return CloudUtil.createCloud(req);
   }


   public static Cloud createCloud(HttpServletRequest req, String sessionname) {
      return CloudUtil.createCloud(req, sessionname);
   }


   public static Map<String, String> getUserCredentials(String username, String password) {
      return CloudUtil.getUserCredentials(username, password);
   }


   public static Cloud getCloudFromSession(HttpServletRequest request) {
      return CloudUtil.getCloudFromSession(request);
   }


   public static Cloud getCloudFromSession(HttpServletRequest request, String sessionname) {
      return CloudUtil.getCloudFromSession(request, sessionname);
   }


   /**
    * Checks if a cloud is on the session with given the default sessionname.
    *
    * @param request
    *           HttpServletRequest to search for the cloud.
    * @return true if a cloud is found, false otherwise.
    */
   public static boolean hasCloud(HttpServletRequest request) {
      return CloudUtil.hasCloud(request);
   }


   /**
    * Checks if a cloud is on the session with given sessionname.
    *
    * @param request
    *           HttpServletRequest to search for the cloud.
    * @param sessionname
    *           The name of the cloud on the session.
    * @return true if a cloud is found, false otherwise.
    */
   public static boolean hasCloud(HttpServletRequest request, String sessionname) {
      return CloudUtil.hasCloud(request, sessionname);
   }


   public static void addToRequest(HttpServletRequest request, String name, Node node) {
      request.setAttribute(name, node);
   }


   public static void addToRequest(HttpServletRequest request, String name, NodeList nodes) {
      request.setAttribute(name, nodes);
   }


   public static void addToRequest(HttpServletRequest request, String name, String value) {
      request.setAttribute(name, value);
   }


   public static void addToRequest(HttpServletRequest request, String name, Collection value) {
      request.setAttribute(name, value);
   }


   public static void addToRequest(HttpServletRequest request, String name, Map<?, ?> value) {
      request.setAttribute(name, value);
   }


   protected void removeFromSession(HttpServletRequest request, ActionForm form) {
      HttpSession session = request.getSession();
      for (Enumeration<String> iter = session.getAttributeNames(); iter.hasMoreElements();) {
         String name = iter.nextElement();
         Object value = session.getAttribute(name);
         if (form == value) { // same reference
            session.removeAttribute(name);
         }
      }
   }

}
