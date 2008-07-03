package org.jahia.jahiawebapps.jforum;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.Servlet;
import javax.servlet.http.*;

import net.jforum.*;

import java.io.*;
import java.util.*;

import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.ConfigKeys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * JahiaWebApp Brigde for JForum
 *
 * @author Khaled TLILI
 */
public class JForumJahiaWebAppBridge extends HttpServlet {
    private final String PROCESS_ACTION_PERFORMED_KEY = "processActionPerformed";
    private final String JFORUM_KEY = "JForum";
    private final String JFORUM_OUPUTSTREAM_RESULT_KEY = "jforum.result";
    private static Logger logger = Logger.getLogger(JForumJahiaWebAppBridge.class);


    /**
     * Init portlet method
     *
     * @param config Description of Parameter
     * @throws ServletException Description of Exception
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Servlet instance = null;
        System.out.println("JForumJahiaWebAppBridge-------------------->init");
        //init
        try {

            // if it's already load JForum App
            if (isAlreadyInstalled()) {
                logger.debug("is already installed");

                // load JForum servlet
                instance = new JForum();

            } else {
                logger.debug("is not already installed");
                instance = new InstallServlet();
            }

            // init Servlet instance
            instance.init(config);

        }
        catch (ServletException ex) {
            ex.printStackTrace();
        }

        // set in context
        getServletContext().setAttribute(JFORUM_KEY, instance);

    }


    /**
     * render method
     *
     * @param request  Description of Parameter
     * @param response Description of Parameter
     * @throws IOException      Description of Exception
     * @throws ServletException Description of Exception
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("Call service method of JForumJahiaWebApp");
        /*
           *  Enumeration enume = request.getAttributeNames();
           *  logger.debug("--- Begin enumerating parameter ---");
           *  while (enume.hasMoreElements()) {
           *  String name = (String) enume.nextElement();
           *  String value = request.getParameter(name);
           *  logger.debug("Found param: " + name + "," + value);
           *  }
           *  logger.debug("--- End enumerating parameter ---");
           */
        logger.debug("Request uri: " + request.getRequestURI());
        logger.debug("QueryString: " + request.getQueryString());
        System.out.println("JForumJahiaWebAppBridge-------------------->service");
        try {
            if (isAlreadyInstalled()) {
                Servlet instance = (Servlet) getServletContext().getAttribute(JFORUM_KEY);
                if (instance instanceof InstallServlet) {
                    instance = new JForum();
                    // init Servlet instance
                    try {
                        instance.init(getServletConfig());
                    }
                    catch (ServletException ex) {
                        printError(response, "Cannot init JForum Servlet due to: " + ex);
                        printError(response, "Service method stopped  !!!");
                        return;
                    }
                    getServletContext().setAttribute(JFORUM_KEY, instance);
                }

                // Manage auto login portal user
                PortalAutoConnectUserManager p = new PortalAutoConnectUserManager(request, response);
                if (p.processSSO()) {
                    // auto action performed
                    logger.debug("Auto action performed");
                    //return;
                } else {
                    logger.debug("No Auto action performed");
                }
            }

            //call JForum service method
            if (!isSameServeltAction(request) || isFirstAction(request)) {
                logger.debug("call service method");
                callServletServiceMethod(request, response);
            } else {
                logger.debug("Jforum service method is NOT executed");
            }

            // print result from session
            printResultFromSession(request.getSession(), response);



        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /**
     * Emulate destroy method
     */
    public void destroy() {
        logger.debug("--- destroying ----");
        Servlet instance = (Servlet) getServletContext().getAttribute(JFORUM_KEY);
        instance.destroy();
    }


    /**
     * Gets the AlreadyInstalled attribute of the JForumPortletBridge object
     *
     * @return The AlreadyInstalled value
     */
    private boolean isAlreadyInstalled() {
        return SystemGlobals.getBoolValue(ConfigKeys.INSTALLED);
    }


    /**
     * Gets the SameServeltAction attribute of the JForumPortletBridge object
     *
     * @param request Description of Parameter
     * @return The SameServeltAction value
     */
    private boolean isSameServeltAction(HttpServletRequest request) {
        return request.getParameter(PROCESS_ACTION_PERFORMED_KEY) == null;
    }


    /**
     * Gets the FirstAction attribute of the JForumPortletBridge object
     *
     * @param request Description of Parameter
     * @return The FirstAction value
     */
    private boolean isFirstAction(HttpServletRequest request) {
        String requestUri = request.getParameter("requestURI");
        return requestUri == null;
    }


    /**
     * emulate a HttpServlettRequest and a HttpServletRespon and call methode
     * service of JForum. This method process also the redirection.
     *
     * @param request  Render requestt object
     * @param response Render response object
     */
    private void callServletServiceMethod(HttpServletRequest request, HttpServletResponse response) {
       System.out.println("JForumJahiaWebAppBridge-------------------->callServletServiceMethod");
        // default case of : Install wizard
        String defaultRequestUri = "install/install.page";
        String defaultModule = "install";
        String defaultAction = "welcome";

        // case of forum
        if (isAlreadyInstalled()) {
            defaultRequestUri = "forums/list.page";
            defaultModule = "forums";
            defaultAction = "list";
        }

        // get HttpServletRequest/response object
        HttpServletRequestWrapper reqW = new HttpServletRequestWrapper(request, defaultRequestUri, defaultModule, defaultAction);
        HttpServletResponseWrapper respW = new HttpServletResponseWrapper(response);

        // get servlet object
        Servlet instance = (Servlet) getServletContext().getAttribute(JFORUM_KEY);

        try {
            // call service method
            // isRedirect = false;
            instance.service(reqW, respW);
            boolean isRedirect = respW.isRedirect();
            if (isRedirect) {
                String redirectUrl = respW.getEncodedRedirect();
                doRedirection(response, redirectUrl);
            }

            // get result as StringBuffer
            String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
            String result = new String(((ServletOutputStreamWrapper) respW.getOutputStream()).getAsByteArray(), encoding);

            // update result
            HttpSession session = request.getSession();
            saveResultInSession(session, result);

        }
        catch (ServletException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            logger.debug("--- --- --");
        }
    }


    /**
     * Description of the Method
     *
     * @param session  Description of Parameter
     * @param response Description of Parameter
     * @throws IOException Description of Exception
     */
    private void printResultFromSession(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String result = (String) session.getAttribute(JFORUM_OUPUTSTREAM_RESULT_KEY);
        logger.debug(result);
        response.getWriter().write(result);
    }


    /**
     * save result in session
     *
     * @param session Description of Parameter
     * @param result  Description of Parameter
     */
    private void saveResultInSession(HttpSession session, String result) {
        session.setAttribute(JFORUM_OUPUTSTREAM_RESULT_KEY, result);
    }


    /**
     * Emulate a response.sendRedirect(...). The redirection is performed by
     * javascript
     *
     * @param response    RenderResponse object
     * @param redirectUrl value of the new url
     * @throws IOException Description of Exception
     */
    private void doRedirection(HttpServletResponse response, String redirectUrl) throws IOException {
        response.setContentType("text/html");
        String script = "<script> document.location='" + redirectUrl + "'; </script>";
        System.out.println("Redirect url: " + redirectUrl);
        response.getOutputStream().write(script.getBytes());
    }


    /**
     * Print error
     *
     * @param response Render response object
     * @param error    Error label
     */
    private void printError(HttpServletResponse response, String error) {
        try {
            response.setContentType("text/html");
            String formattedError = "<div style='color:red;'> Error: " + error + "</div> <br/>";
			response.getOutputStream().write(formattedError.getBytes());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
