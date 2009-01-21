/*
 * Copyright 2003,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finalist.pluto.portalImpl.core;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.window.PortletWindow;

/**
 * PortalURL to accomodate CMSC's filter/servlet setup.
 *
 * @changes pluto-1.0.1
 * @author Wouter Heijke
 */
public class PortalURL {

   private static final String SECURE_PROTOCOL = "https://";
   private static final String INSECURE_PROTOCOL = "http://";

   private static Log log = LogFactory.getLog(PortalURL.class);

   private String basePortalURL = null;
   private boolean secure = false;
   private List<String> startGlobalNavigation = new ArrayList<String>();
   private List<String> startLocalNavigation = new ArrayList<String>();
   private HashMap<String, Object> encodedStartControlParameter = new HashMap<String, Object>();
   private HashMap<String, String> startStateLessControlParameter = new HashMap<String, String>();
   private String host;
   private int port;

   private boolean analyzed = false;
   private PortalEnvironment environment;


   /**
    * Creates and URL pointing to the home of the portal
    *
    * @param request
    *           the servlet request
    * @return the portal URL
    */
   public String getBasePortalURL(HttpServletRequest request) {
      return getBasePortalURL(PortalEnvironment.getPortalEnvironment(request));
   }


   /**
    * Creates and URL pointing to the home of the portal
    *
    * @param env
    *           the portal environment
    * @return the portal URL
    */
   public String getBasePortalURL(PortalEnvironment env) {
      StringBuffer result = new StringBuffer(256);
      if (env == null) {
         result.append(basePortalURL);
      }
      else {
         /*
          * result.append(secure?hostNameHTTPS:hostNameHTTP);
          */
         result.append(env.getRequest().getContextPath());
      }

      return result.toString();
   }


   /**
    * Creates and URL pointing to the home of the portal
    *
    * @return the portal URL
    */
   public String getBasePortalURL() {
      return basePortalURL;
   }


   public PortalURL(String host, HttpServletRequest request, String globalNavigation) {
      this(request.getContextPath(), request.isSecure(), request.getServerPort(), globalNavigation);
      this.host = host;
   }


   private PortalURL(String basePortalURL, boolean secure, int port, String globalNavigation) {
      this.basePortalURL = basePortalURL;
      this.secure = secure;
      this.port = port;
      if (globalNavigation != null) {
         addGlobalNavigation(globalNavigation);
      }
      analyzed = true;
   }


   /**
    * Creates and URL pointing to the home of the portal
    *
    * @param env
    *           the portal environment
    */
   public PortalURL(PortalEnvironment env) {
      environment = env;
      this.secure = env.getRequest().isSecure();
      this.port = env.getRequest().getServerPort();
      this.basePortalURL = env.getRequest().getContextPath();
   }


   /**
    * Creates and URL pointing to the home of the portal
    *
    * @param request
    *           the servlet request
    */
   public PortalURL(HttpServletRequest request) {
      this(PortalEnvironment.getPortalEnvironment(request));
   }


   /**
    * Adds a navigational information pointing to a portal part, e.g. PageGroups
    * or Pages
    *
    * @param nav
    *           the string pointing to a portal part
    */
   public final void addGlobalNavigation(String nav) {
      startGlobalNavigation.add(nav);
   }


   /**
    * Sets the local navigation. Because the local navigation is always handled
    * by the Browser, therefore the local navigation cleared.
    */
   public void setLocalNavigation() {
      startLocalNavigation = new ArrayList<String>();
   }


   /**
    * Adds a navigational information pointing to a local portal part inside of
    * a global portal part, for example, a portlet on a page.
    *
    * @param nav
    *           the string pointing to a local portal part
    */
   public void addLocalNavigation(String nav) {
      startLocalNavigation.add(nav);
   }


   /**
    * Returns true if the given string is part of the global navigation of this
    * URL
    *
    * @param nav
    *           the string to check
    * @return true, if the string is part of the navigation
    */
   public boolean isPartOfGlobalNavigation(String nav) {
      return startGlobalNavigation.contains(nav);
   }


   /**
    * Returns true if the given string is part of the local navigation of this
    * URL
    *
    * @param nav
    *           the string to check
    * @return true, if the string is part of the navigation
    */
   public boolean isPartOfLocalNavigation(String nav) {
      return startLocalNavigation.contains(nav);
   }


   private String urlEncode(String value) {
      // value = URLEncoder.encode(value);
      // java.net.URLEncoder encodes space (' ') as a plus sign ('+'),
      // instead of %20 thus it will not be decoded properly by tomcat when
      // the
      // request is parsed. Therefore replace all '+' by '%20'.
      // If there would have been any plus signs in the original string, they
      // would
      // have been encoded by URLEncoder.encode()
      // control = control.replace("+", "%20");//only works with JDK 1.5
      // value = value.replaceAll("\\+", "%20");
      return value;
   }


   public String getGlobalNavigationAsString() {
      StringBuffer result = new StringBuffer(200);
      Iterator<String> iterator = startGlobalNavigation.iterator();
      if (iterator.hasNext()) {
         result.append(iterator.next());
         while (iterator.hasNext()) {
            result.append('/');
            String st = iterator.next();
            result.append(st);
         }
      }
      return result.toString();
   }


   public String getLocalNavigationAsString() {
      StringBuffer result = new StringBuffer(30);
      Iterator<String> iterator = startLocalNavigation.iterator();
      if (iterator.hasNext()) {
         result.append(iterator.next());
         while (iterator.hasNext()) {
            result.append(".");
            result.append(iterator.next());
         }
      }
      return result.toString();
   }


   public String getControlParameterAsString(PortalControlParameter controlParam) {
      Map<String, Object> encodedStateFullParams = encodedStartControlParameter;
      if (controlParam != null) {
         encodedStateFullParams = controlParam.getEncodedStateFullControlParameter();
      }

      StringBuffer result = new StringBuffer(100);
      // sort encodedNames in the natural order.
      // Search spiders usually use the url as key for a html document.
      // This makes portal urls more the same instead of the random order of the hashmap
      Set<String> encodedNames = new TreeSet<String>(encodedStateFullParams.keySet());
      Iterator<String> iterator = encodedNames.iterator();
      while (iterator.hasNext()) {
         result.append('/');
         String encodedName = iterator.next();
         String encodedValue = (String) encodedStateFullParams.get(encodedName);
         if (encodedValue != null) {
            // appends the prefix (currently "_") in front of the encoded
            // parameter name
            result.append(PortalControlParameter.encodeParameterName(encodedName));
            result.append('/');
            result.append(urlEncode(encodedValue));
         }
      }

      return result.toString();
   }


   public String getRequestParameterAsString(PortalControlParameter controlParam) {
      if (controlParam != null) {
         Map<String, String[]> requestParams = controlParam.getRequestParameter();
         StringBuffer result = new StringBuffer(100);
         Iterator<String> iterator = requestParams.keySet().iterator();
         boolean hasNext = iterator.hasNext();
         if (hasNext) {
            result.append("?");
         }

         while (hasNext) {
            String name = iterator.next();
            Object value = requestParams.get(name);
            String[] values = value instanceof String ? new String[] { (String) value } : (String[]) value;

            result.append(urlEncode(name));
            result.append("=");
            result.append(urlEncode(values[0]));
            for (int i = 1; i < values.length; i++) {
               result.append("&");
               result.append(urlEncode(name));
               result.append("=");
               result.append(urlEncode(values[i]));
            }

            hasNext = iterator.hasNext();
            if (hasNext) {
               result.append("&");
            }
         }

         return result.toString();
      }
      return "";
   }


   public String getPort(boolean secure) {
      if ((!secure && port != 80) || (secure && port != 443)) {
         return ":" + port;
      }
      return "";
   }


   @Override
   public String toString() {
      return toString(null, null);
   }


   public String toString(PortalControlParameter controlParam, Boolean p_secure) {

      StringBuffer url = new StringBuffer(256);

      boolean secure = false;
      if (p_secure != null) {
         secure = p_secure.booleanValue();
      }
      else {
         secure = this.secure;
      }

      if (host != null) {
         url.append(secure ? SECURE_PROTOCOL : INSECURE_PROTOCOL);
         url.append(host);
         url.append(getPort(secure));
      }
      url.append(getBasePortalURL());

      String global = getGlobalNavigationAsString();
      if (global.length() > 0) {
         url.append('/');
         url.append(global);
      }

      String control = getControlParameterAsString(controlParam);
      if (control.length() > 0) {
         url.append(control);
      }

      String requestParam = getRequestParameterAsString(controlParam);
      if (requestParam.length() > 0) {
         url.append(requestParam);
      }

      String local = getLocalNavigationAsString();
      if (local.length() > 0) {
         url.append("#");
         url.append(local);
      }

      if (environment == null) {
         return url.toString();
      }
      else {
         return environment.getResponse().encodeURL(url.toString());
      }
   }


   Map<String, Object> getClonedEncodedStateFullControlParameter() {
      analyzeRequestInformation();
      return (Map<String, Object>) encodedStartControlParameter.clone();
   }


   Map<String, String> getClonedStateLessControlParameter() {
      analyzeRequestInformation();
      return (Map<String, String>) startStateLessControlParameter.clone();
   }


   void analyzeControlInformation(PortalControlParameter control) {
      encodedStartControlParameter = (HashMap<String, Object>) control.getEncodedStateFullControlParameter();
      startStateLessControlParameter = (HashMap<String, String>) control.getStateLessControlParameter();
   }


   void analyzeRequestInformation() {
      if (analyzed) {
         return;
      }

      startGlobalNavigation = new ArrayList<String>();
      startLocalNavigation = new ArrayList<String>();
      encodedStartControlParameter = new HashMap<String, Object>();
      startStateLessControlParameter = new HashMap<String, String>();

      // check the complete pathInfo for
      // * navigational information
      // * control information
      String pathInfo = environment.getRequest().getServletPath();
      if (pathInfo != null) {
         StringTokenizer tokenizer = new StringTokenizer(pathInfo, "/");

         int mode = 0; // 0=navigation, 1=control information
         String encodedName = null;
         while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            log.debug("$$$  token='" + token + "'");
            if (PortalControlParameter.isControlParameter(token)) {
               mode = 1;
               encodedName = token;
            }
            else if (mode == 0) {
               startGlobalNavigation.add(token);
            }
            else if (mode == 1) {
               if ((PortalControlParameter.isStateFullParameter(encodedName))) {
                  // cut the prefix before saving the parameter name
                  encodedName = PortalControlParameter.decodeParameterName(encodedName);
                  encodedStartControlParameter.put(encodedName, token);
               }
               else {
                  startStateLessControlParameter.put(PortalControlParameter.decodeParameterName(encodedName),
                        PortalControlParameter.decodeParameterValue(encodedName, token));
               }
               mode = 0;
            }
         }
      }
      analyzed = true;

   }


   public void setRenderParameter(PortletWindow portletWindow, String name, String[] values) {
      encodedStartControlParameter.put(PortalControlParameter.encodeRenderParamName(portletWindow, name),
            PortalControlParameter.encodeRenderParamValues(values));

   }


   public void setRenderParameter(String portletWindow, String name, String[] values) {
      encodedStartControlParameter.put(PortalControlParameter.encodeRenderParamName(portletWindow, name),
            PortalControlParameter.encodeRenderParamValues(values));

   }


   public void clearRenderParameters(PortletWindow portletWindow) {
      String prefix = PortalControlParameter.getRenderParamKey(portletWindow);
      Iterator<String> keyIterator = encodedStartControlParameter.keySet().iterator();
      while (keyIterator.hasNext()) {
         String name = keyIterator.next();
         if (name.startsWith(prefix)) {
            keyIterator.remove();
         }
      }
   }

}
