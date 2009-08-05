package org.jahia.portlet.test;

import javax.portlet.*;
import java.util.*;

/**
 * TestingPortlet
 *
 * @author Khaled TLILI
 */
public class TestPortlet extends GenericPortlet {

   /**
    * Init portlet method
    *
    * @param config Description of Parameter
    * @throws PortletException Description of Exception
    */
   public void init(PortletConfig config) throws PortletException {
      super.init(config);

   }


   /**
    * processAction method
    *
    * @param request  Description of Parameter
    * @param response Description of Parameter
    */
   public void processAction(ActionRequest request, ActionResponse response) {
      System.err.println("====== Begin process method ======");
      // send parameter to render request phase
      Enumeration paramsName = request.getParameterNames();
      // request.setCharacterEncoding(SystemGlobals.getValue(ConfigKeys.ENCODING));
      while (paramsName.hasMoreElements()) {
         String name = (String) paramsName.nextElement();
         String[] values = request.getParameterValues(name);
         response.setRenderParameter(name, values);
         System.err.println("Found parameter in processAction method: " + name + "," + values[0]);
      }
      System.err.println("====== Finish process method ======");

   }


   /**
    * render method
    *
    * @param request  Description of Parameter
    * @param response Description of Parameter
    */
   public void render(RenderRequest request, RenderResponse response) {
      testParameter(request);
      testSessionAttribut(request);
      testUserPrincipal(request);
   }


   /**
    * destroy method
    */
   public void destroy() {
      System.err.println("====== destroying ======");
   }


   /**
    * A unit test for JUnit
    *
    * @param request Description of Parameter
    */
   private void testUserPrincipal(RenderRequest request) {
      java.security.Principal userP = request.getUserPrincipal();
      if (userP != null) {
         System.out.println("User principal is: " + userP.getName());

      } else {
         System.out.println("User principal is null");
      }
      System.out.println("Remote user is: " + request.getRemoteUser());
   }


   /**
    * A unit test for JUnit
    *
    * @param request Description of Parameter
    */
   private void testParameter(RenderRequest request) {
      System.err.println("====== Begin get param from a renderRequest Object ======");
      Enumeration paramsName = request.getParameterNames();
      while (paramsName.hasMoreElements()) {
         String name = (String) paramsName.nextElement();
         String[] values = request.getParameterValues(name);
         System.err.println("Found parameter,render method: " + name + "," + values[0]);
      }
      //validate procees action
      System.err.println("====== Finish get param ======");
   }


   /**
    * A unit test for JUnit
    *
    * @param request Description of Parameter
    */
   private void testSessionAttribut(RenderRequest request) {
      System.err.println("====== Begin Test Session Attribut  ======");
      // Get portlet Session
      PortletSession pSession = request.getPortletSession();

      // test if it's a new session
      System.err.println("Test is new Session: " + pSession.isNew());
      System.err.println("Session ID: " + pSession.getId());
      System.err.println("Creation Time: " + pSession.getCreationTime());

      //look for testAttribut in session
      String testAttribut = (String) pSession.getAttribute("test.user");
      if (testAttribut == null) {
         System.err.println("testAttribut not found in session");
         testAttribut = request.getRemoteUser();
         pSession.setAttribute("test.user", testAttribut);
      } else {
         System.err.println("testAttribut is found in session whit value: " + testAttribut);
      }

      //Print
      System.err.println("Test Attribut value: " + testAttribut);

      // print all session att
      System.err.println("All session Attribut value: " + testAttribut);
      Enumeration enume = pSession.getAttributeNames();
      while (enume.hasMoreElements()) {
         String name = (String) enume.nextElement();
         String value = (String) pSession.getAttribute(name);
         System.err.println("Found att: " + name + " with value: " + value);
      }

      System.err.println("====== End Test Session Attribut  ======");

	}

}
