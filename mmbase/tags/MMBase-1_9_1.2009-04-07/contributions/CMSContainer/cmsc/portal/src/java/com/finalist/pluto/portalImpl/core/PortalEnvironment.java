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

import java.util.Arrays;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PortalEnvironment {

   public final static String REQUEST_PORTALENV = "com.finalist.pluto.portalImpl.core.PortalEnvironment";

   private HttpServletRequest request;

   private HttpServletResponse response;

   private PortalURL requestedPortalURL;

   private PortalControlParameter portalControl;

   private String requestedMimetype;

   public PortalEnvironment(HttpServletRequest request, HttpServletResponse response) {
      this.request = request;
      this.response = response;

      requestedPortalURL = new PortalURL(this);
      // get navigational information and prepare PortalURL object
      requestedPortalURL.analyzeRequestInformation();
      portalControl = new PortalControlParameter(requestedPortalURL);

      registerEnvironment();
   }


   public final void registerEnvironment() {
      // set Environment in Request for later use
      this.request.setAttribute(REQUEST_PORTALENV, this);
   }


   public static PortalEnvironment getPortalEnvironment(HttpServletRequest request) {
      return (PortalEnvironment) request.getAttribute(REQUEST_PORTALENV);
   }

   public static PortalEnvironment getPortalEnvironment(PortletRequest request) {
       return (PortalEnvironment) request.getAttribute(REQUEST_PORTALENV);
   }


   public HttpServletRequest getRequest() {
      return request;
   }


   public HttpServletResponse getResponse() {
      return response;
   }

   public PortalURL getRequestedPortalURL() {
      return requestedPortalURL;
   }


   public PortalControlParameter getPortalControlParameter() {
      return portalControl;
   }


   public void changeRequestedPortalURL(PortalURL url, PortalControlParameter control) {
      requestedPortalURL = url;
      requestedPortalURL.analyzeControlInformation(control);
      portalControl = control;
   }

   public List<String> getAcceptContentTypes() {
       String acceptHeader = request.getHeader("accept");
       String[] contentTypes = acceptHeader.split(",");
       return Arrays.asList(contentTypes);
   }

    public String getRequestedMimetype() {
        return requestedMimetype;
    }

    public void setRequestedMimetype(String requestedMimetype) {
        this.requestedMimetype = requestedMimetype;
    }
}
