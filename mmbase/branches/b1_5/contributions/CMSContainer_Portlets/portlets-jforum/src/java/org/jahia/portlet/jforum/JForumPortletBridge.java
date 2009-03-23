package org.jahia.portlet.jforum;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import net.jforum.InstallServlet;
import net.jforum.JForum;
import net.jforum.JForumExecutionContext;
import net.jforum.context.ForumContext;
import net.jforum.context.JForumContext;
import net.jforum.context.ResponseContext;
import net.jforum.context.web.WebRequestContext;
import net.jforum.context.web.WebResponseContext;
import net.jforum.util.legacy.commons.fileupload.FileItem;
import net.jforum.util.legacy.commons.fileupload.FileUploadException;
import net.jforum.util.legacy.commons.fileupload.RequestContext;
import net.jforum.util.legacy.commons.fileupload.disk.DiskFileItemFactory;
import net.jforum.util.legacy.commons.fileupload.servlet.ServletFileUpload;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.portlet.fileupload.PortletRequestContext;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.CmscPortlet;
import com.finalist.cmsc.util.ServerUtil;

import freemarker.template.SimpleHash;

/**
 * Portlet Brigde for JForum
 *
 * @author Khaled TLILI
 */
public class JForumPortletBridge extends CmscPortlet {
    private static final String MULTIPART = "multipart/";

    private final String JFORUM_KEY = "JForum";
    /**
     * Description of the Field
     */
    public static final String JFORUM_OUPUTSTREAM_RESULT_KEY = "jforum.result";
    /**
     * flag to know if porcessAction has been called
     */
    public static final String PROCESS_ACTION_PERFORMED_KEY = "processActionPerformed";
    /**
     * flag to know if porcessAction has been called
     */
    public static final String FILE_UPLOAD_LIST_KEY = "org.jahia.portlet.jforum.fileupload.map";
    private static Logger logger = Logger.getLogger(JForumPortletBridge.class);


    /**
     * Init portlet method
     *
     * @param config Description of Parameter
     * @throws PortletException Description of Exception
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        Servlet instance = null;
        if (null != getPortletContext().getAttribute(JFORUM_KEY)) return;

        //init
        try {

            // if it's already load JForum App
            if (isAlreadyInstalled()) {
                logger.info("JForum is already installed");
				 instance = new JForum();
                // load JForum servlet

            } else {
                logger.info("is not already installed");
                instance = new InstallServlet();
            }
            // init Servlet instance
            instance.init(new ServletConfigWrapper(config));

        }
        catch (ServletException ex) {
            ex.printStackTrace();
        }

        // set in context
		getPortletContext().setAttribute("config", config);
        getPortletContext().setAttribute(JFORUM_KEY, instance);

    }


    /**
     * processAction method
     *
     * @param request  Description of Parameter
     * @param response Description of Parameter
     */
    public void processAction(ActionRequest request, ActionResponse response) {
        try {
            request.setCharacterEncoding(SystemGlobals.getValue(ConfigKeys.ENCODING));
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        logger.debug("-- Begin process method--");
        // send parameter to render request phase
        Enumeration paramsName = request.getParameterNames();

        while (paramsName.hasMoreElements()) {
            String name = (String) paramsName.nextElement();
            String[] values = request.getParameterValues(name);
            response.setRenderParameter(name,values);

        }
        String postBody = "";
        //deal with file upload
        boolean isFileUpload = isMultipartContent(request);
        if (isFileUpload) {
            postBody = handleMultipartRequest(request, response);
        } else {
            logger.debug("It's no a file Upload.");
        }

        request.getPortletSession().setAttribute("postBody", postBody);
        //validate procees action
        validateProcessAction(request);

        logger.debug("-- Finish process method--");

    }


    /**
     * render method
     *
     * @param request  Description of Parameter
     * @param response Description of Parameter
     */
    public void render(RenderRequest request, RenderResponse response) {
        logger.debug("Begin render method");
        String postBody = (String) request.getPortletSession().getAttribute("postBody");

        // deal with SSO
//        updateRemoteUser(request);

        try {
            // default values for reuqest wrapper
            String defaultRequestUri = "";
            String defaultModule;
            String defaultAction;
            // Manage auto login portal user
            PortalAutoConnectUserManager userProcesseur = new PortalAutoConnectUserManager(request, response);
            if(ServerUtil.isLive()) {
               String stagingPath = PropertiesUtil.getProperty("system.stagingpath");
               if(StringUtils.isEmpty(stagingPath)) {
                  logger.info("Properity system.stagingpath is null");
               }
               stagingPath = checkSlash(stagingPath);
               defaultRequestUri = stagingPath+"forums/list.page";
            }
            else {
               String contextPath = request.getContextPath();
               contextPath = checkSlash(contextPath);
               defaultRequestUri += contextPath+"forums/list.page";
            }
           // defaultRequestUri = "forums/list.page";
            defaultModule = "forums";
            defaultAction = "list";
            if (isAlreadyInstalled()) {
                // if (false) {
                logger.debug("JForum is already installed");
                Servlet instance = (Servlet) getPortletContext().getAttribute(JFORUM_KEY);

                logger.debug("Database is: " + SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER));
                if (instance instanceof InstallServlet) {
                    logger.debug("Switch to JForum");
                    instance = new JForum();
                    // init Servlet instance
                    try {
                        instance.init(new ServletConfigWrapper(getPortletConfig()));
                    }
                    catch (ServletException ex) {
                        logger.error("Error has occured", ex);
                        printError(response, "Cannot init JForum Servlet due to: " + ex);
                        printError(response, "Render method stopped  !!!");
                        return;
                    }
                    getPortletContext().setAttribute(JFORUM_KEY, instance);
                }

                if (userProcesseur.processSSO()) {
                    // auto action performed
                    logger.debug("Auto action performed");
                } else {
                    logger.debug("No Auto action performed");
                }

                if (userProcesseur.isRedirect()) {
                    return;
                }
            } else {
                logger.info("Is not already installed");

                // default case of : Install wizard
            }

            //call JForum service method
            if (!isSameServeltAction(request) || isFirstAction(request)) {
                // || !userProcesseur.isNotAllowedAction()) {
                callServletServiceMethod(request, response, defaultRequestUri, defaultModule, defaultAction, postBody);
            }

            //invalidate process Action call
            unvalidateAll(request);

            // print result from session
            printResultFromSession(request, response);

        }
        catch (Exception ex) {
            logger.error("Error has occured", ex);
        }
        logger.debug("End render method");

    }

   private String checkSlash(String path) {
      if (!path.endsWith("/")) {
         path += "/";
      }
      return path;
   }

    private void updateRemoteUser(RenderRequest request) {
        // set username in session (SSO purpose)
        PortletSession session = request.getPortletSession();
        String remoteUser = request.getRemoteUser();
        String sessionRemoteUser = (String) session.getAttribute("JFORUM_USERNAME", PortletSession.APPLICATION_SCOPE);
        if (remoteUser == null) {
            logger.debug("Guest user");
            if (sessionRemoteUser != null) {
                logger.debug("update session remote user --> set to guest");
                session.removeAttribute("JFORUM_USERNAME", PortletSession.APPLICATION_SCOPE);
            }
        } else {
            logger.debug("update session remote user --> set to " + remoteUser);
            session.setAttribute("JFORUM_USERNAME", remoteUser, PortletSession.APPLICATION_SCOPE);
        }
    }

    private void unvalidateAll(RenderRequest request) {
        // invalidate process action
        invalidateProcessAction(request);

        //invalidate file upload
        if (isMultipart(request)) {
            invalidateFileUpload(request);
        }
    }


    /**
     * Emulate destroy method
     */
    public void destroy() {
        System.err.println("--- [JForum portlet bridge] destroying ----");
        Servlet instance = (Servlet) getPortletContext().getAttribute(JFORUM_KEY);
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
    private boolean isSameServeltAction(RenderRequest request) {
        PortletSession session = request.getPortletSession();
        boolean result = session.getAttribute(PROCESS_ACTION_PERFORMED_KEY) == null;
        logger.debug("is same action? " + result);
        return result;
    }


    /**
     * Gets the FirstAction attribute of the JForumPortletBridge object
     *
     * @param request Description of Parameter
     * @return The FirstAction value
     */
    private boolean isFirstAction(RenderRequest request) {
        String requestUri = request.getParameter("requestURI");
        return requestUri == null;
    }


    /**
     * hadle multipart request
     *
     * @param actionRequest Description of Parameter
     * @return The FileUpload value
     */
    private String handleMultipartRequest(ActionRequest actionRequest, ActionResponse actionResponse) {
        String postBody = "";
        List itemsObject = new ArrayList();
        String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
        // check if it's multipart file
        logger.debug("Get content type: " + actionRequest.getContentType());
        String tmpDir = SystemGlobals.getApplicationPath() + "/" + SystemGlobals.getValue(ConfigKeys.TMP_DIR);

        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(100 * 1024, new File(tmpDir)));
        upload.setHeaderEncoding(encoding);
        try {
            File tmpDirFile = new File(tmpDir);
            if (!tmpDirFile.exists()) {
                boolean result = tmpDirFile.createNewFile();
                logger.debug("Create " + tmpDirFile + ": " + result);
            }
            RequestContext context = new PortletRequestContext(actionRequest);
            List items = upload.parseRequest(context);
            for (Iterator iter = items.iterator(); iter.hasNext();) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    logger.debug("Process upload, form field Name: " + item.getFieldName());

                    if ("message".equals(item.getFieldName())) {
                        postBody = item.getString(encoding);
                        actionResponse.setRenderParameter(item.getFieldName(),"");
                    } else {

                      String fieldName = item.getFieldName();
                       String fieldValue = item.getString();

                       if(StringUtils.isNotEmpty(fieldValue)) {
                          if(fieldValue.indexOf("#") == -1) {
                             actionResponse.setRenderParameter(fieldName, fieldValue);
                          }
                       }
                    }
                } else {
                    if (item.getSize() > 0) {
                        logger.debug("Process upload, field Name: " + item.getFieldName());
                        itemsObject.add(item);
                    }
                }
            }
            validateFileUpload(actionRequest, itemsObject);
            logger.debug("File upload done");

        }
        catch (FileUploadException e) {
            //new ForumException(e);
            logger.error("File upload error due to: ", e);
            e.printStackTrace();
        }
        catch (Exception e) {
            //new ForumException(e);
            logger.error("File upload error due to: ", e);
            e.printStackTrace();
        }

        return postBody;
    }

    private final boolean isMultipartContent(ActionRequest req) {
        String contentType = req.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }

    private boolean isMultipart(RenderRequest request) {
        PortletSession session = request.getPortletSession();
        boolean result = session.getAttribute(FILE_UPLOAD_LIST_KEY) != null;
        logger.debug("is same file upload? " + result);
        return result;
    }


    /**
     * Validate process Action
     *
     * @param request Description of Parameter
     */
    private void validateProcessAction(PortletRequest request) {
        logger.debug("Validate process Action");
        PortletSession session = request.getPortletSession();
        session.setAttribute(PROCESS_ACTION_PERFORMED_KEY, "1");
    }


    /**
     * Invalidate process Action
     *
     * @param request Description of Parameter
     */
    private void invalidateProcessAction(PortletRequest request) {
        logger.debug("invalidate process Action");
        PortletSession session = request.getPortletSession();
        session.removeAttribute(PROCESS_ACTION_PERFORMED_KEY);
    }

    /**
     * Validate process Action
     *
     * @param request Description of Parameter
     */
    private void validateFileUpload(PortletRequest request, List items) {
        logger.debug("Validate file upload");
        PortletSession session = request.getPortletSession();
        session.setAttribute(FILE_UPLOAD_LIST_KEY, items);
    }

    private void synchFileUpload(PortletRequest request) {
        PortletSession session = request.getPortletSession();
        List items = (List) session.getAttribute(FILE_UPLOAD_LIST_KEY);
        if (items != null) {
            logger.debug("Synch file upload, found (" + items.size() + ") files.");
            for (int i = 0; i < items.size(); i++) {
                net.jforum.util.legacy.commons.fileupload.FileItem item = (net.jforum.util.legacy.commons.fileupload.FileItem) items.get(i);
                request.setAttribute(item.getFieldName(), item);

            }
            session.removeAttribute(FILE_UPLOAD_LIST_KEY);
        } else {
            logger.debug("It'snot a file upload request.");
        }
    }


    /**
     * Invalidate process Action
     *
     * @param request Description of Parameter
     */
    private void invalidateFileUpload(PortletRequest request) {
        logger.debug("invalidate file upload");
        PortletSession session = request.getPortletSession();
        session.removeAttribute(FILE_UPLOAD_LIST_KEY);
    }


    /**
     * emulate a HttpServlettRequest and a HttpServletRespon and call methode
     * service of JForum. This method process also the redirection.
     *
     * @param request           Render requestt object
     * @param response          Render response object
     * @param defaultRequestUri Description of Parameter
     * @param defaultModule     Description of Parameter
     * @param defaultAction     Description of Parameter
     */
    private void callServletServiceMethod(RenderRequest request, RenderResponse response, String defaultRequestUri, String defaultModule, String defaultAction, String postBody) {
        // deal with file upload
        synchFileUpload(request);
        HttpServletResponseWrapper respW = new HttpServletResponseWrapper(response);
        HttpServletRequestWrapper reqW = new HttpServletRequestWrapper(request, defaultRequestUri, defaultModule, defaultAction, HttpServletRequestWrapper.HTTP_GET, postBody);

        if (!isAlreadyInstalled()) {
           
           Locale locale = (Locale)(request.getPortletSession().getAttribute("javax.servlet.jsp.jstl.fmt.locale.session"));
           String language = "en_US";
           String charset = (String)request.getPortletSession().getAttribute("javax.servlet.jsp.jstl.fmt.request.charset");

           if(!locale.getLanguage().equals("en") && StringUtils.isEmpty(locale.getCountry())) {
              locale = Locale.getDefault();
              language = locale.getLanguage()+"_"+locale.getCountry();
           }
           install(respW, reqW,language,charset);
		   PortletConfig config = (PortletConfig)getPortletContext().getAttribute("config");
		   JForum instance = new JForum();
		   try {
		    instance.init(new ServletConfigWrapper(config));
		   }
		   catch (ServletException ex) {
            ex.printStackTrace();
           }

		   getPortletContext().setAttribute(JFORUM_KEY,instance);
		   getPortletContext().removeAttribute("config");
        }
        // get servlet object
        Servlet instance = (Servlet) getPortletContext().getAttribute(JFORUM_KEY);
        try {
            // call service method
            // isRedirect = false;
            instance.service(reqW, respW);
            boolean isRedirect = respW.isRedirect();
            if (isRedirect) {
                logger.debug("There is a redirection");
                String redirectUrl = respW.getEncodedRedirect();
                doRedirection(response, redirectUrl);
            } else {
                logger.debug("There is NO redirection");
            }

            // get result as StringBuffer
            String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
            String result = new String(((ServletOutputStreamWrapper) respW.getOutputStream()).getAsByteArray(), encoding);
            // update result
            PortletSession session = request.getPortletSession();
            saveResultInSession(session, result);
            logger.debug(" End callServletServiceMethod ");
        }
        catch (Exception ex) {
            logger.error("Error has occured", ex);
        }
    }


   private void install(HttpServletResponseWrapper respW, HttpServletRequestWrapper reqW,String language,String charset) {

           reqW.setAttribute("language", language);
           reqW.setAttribute("database", StringUtils.isEmpty(SystemGlobals.getValue("database.driver.name"))?"mysql":SystemGlobals.getValue("database.driver.name"));
           reqW.setAttribute("dbencoding", StringUtils.isEmpty(charset)?"utf-8":charset);
           reqW.setAttribute("dbencoding_other", "");
           reqW.setAttribute("use_pool", "true");
           reqW.setAttribute("forum_link", "");
           reqW.setAttribute("admin_pass1",StringUtils.isEmpty(SystemGlobals.getValue("admin.password"))?"admin2k":SystemGlobals.getValue("admin.password"));
           reqW.setAttribute("db_connection_type", "ds");
           reqW.setAttribute("site_link", "");
           reqW.setAttribute("dbdatasource", StringUtils.isEmpty(SystemGlobals.getValue("database.dbdatasource"))?"java:comp/env/jdbc/jforum":SystemGlobals.getValue("database.dbdatasource"));
           try {
              net.jforum.context.RequestContext requestContext = new WebRequestContext(reqW);
              ResponseContext responseContext = new WebResponseContext(respW);

              ForumContext forumContext = new JForumContext(
                  requestContext.getContextPath(),
                  SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION),
                  requestContext,
                  responseContext,
                  false
              );
              JForumExecutionContext ex = JForumExecutionContext.get();
              ex.setForumContext( forumContext );

            JForumExecutionContext.set(ex);
            
            // ActionServletRequest reqAW1 = new ActionServletRequest(reqW);
//             JForumContext jforumcontext = new JForumContext(reqW
//                  .getContextPath(), SystemGlobals
//                  .getValue(ConfigKeys.SERVLET_EXTENSION), reqW, respW, false);
//
//             reqAW1.setJForumContext(jforumcontext);
//
//             JForumExecutionContext ex = JForumExecutionContext.get();
//             ex.setResponse(respW);
//             ex.setRequest(reqAW1);
//
//            // Assigns the information to user's thread
//             JForumExecutionContext.set(ex);

             // Context
             SimpleHash context = JForumExecutionContext.getTemplateContext();

             InstallAction command;

             command = new InstallAction(requestContext, context);
             command.checkInformation();
             command.doInstall();
             command.finished();
         } 
         catch (IOException e) {
            logger.debug("install io error: " +e.getMessage());
         } 
         catch (Exception e) {
            logger.debug("install error: " + e.getMessage());
         }
   }

    private void logRequestAttributes(RenderRequest request) {
        Enumeration enume = request.getAttributeNames();
        while (enume.hasMoreElements()) {
            logger.debug("Found attr: " + enume.nextElement());
        }
    }


    /**
     * Print result from session
     *
     * @param request  Description of Parameter
     * @param response Description of Parameter
     * @throws java.io.IOException Description of Exception
     */
    private void printResultFromSession(RenderRequest request, RenderResponse response) throws java.io.IOException {
        response.setContentType("text/html;charset=" + SystemGlobals.getValue(ConfigKeys.ENCODING));
        //response.setContentType("text/html;");

        String result = (String) request.getPortletSession().getAttribute(JFORUM_OUPUTSTREAM_RESULT_KEY);
        if (result == null || result.equalsIgnoreCase("")) {
            /*
                *  logger.debug("No result in session");
                *  // default case of : Install wizard
                *  String defaultRequestUri = "install/install.page";
                *  String defaultModule = "install";
                *  String defaultAction = "welcome";
                *  // case of forum
                *  if (isAlreadyInstalled()) {
                *  defaultRequestUri = "forums/list.page";
                *  defaultModule = "forums";
                *  defaultAction = "list";
                *  }
                *  callServletServiceMethod(request, response, defaultRequestUri, defaultModule, defaultAction);
                */
            logger.error("Result is null or empty: " + result);
            result = "<div style='color:red;'> Error: Please logout and log again </div> <br/>";
        }

        logger.debug("Print result from session");
        response.getPortletOutputStream().write(result.getBytes(SystemGlobals.getValue(ConfigKeys.ENCODING)));

        /* UserSession us = SessionFacade.getUserSession();
       int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
       if (us.getUserId() != anonymousUser) {
           logger.info("Connected user has same id than anonymous");
       } */

    }


    /**
     * save result in session
     *
     * @param session Description of Parameter
     * @param result  Description of Parameter
     */
    private void saveResultInSession(PortletSession session, String result) {
        if (result == null) {
            logger.error("Can't save parameter in session, result is  null");
            //result = "<div style='color:red;'> Error: Please logout and log again </div> <br/>";
        } else if (result.equals("")) {
            logger.warn("Result is empty");
            session.setAttribute(JFORUM_OUPUTSTREAM_RESULT_KEY, result);
        } else {
            logger.debug("Update HTML result in session");
            session.setAttribute(JFORUM_OUPUTSTREAM_RESULT_KEY, result);
        }
    }


    /**
     * Emulate a response.sendRedirect(...). The redirection is performed by
     * javascript
     *
     * @param response    RenderResponse object
     * @param redirectUrl value of the new url
     * @throws java.io.IOException Description of Exception
     */
    private void doRedirection(RenderResponse response, String redirectUrl) throws java.io.IOException {
        response.setContentType("text/html");
        String script = "<script> document.location='" + redirectUrl + "'; </script>";
        logger.debug("Redirect url: " + redirectUrl);
        logger.debug(script);
        response.getPortletOutputStream().write(script.getBytes());

    }


    /**
     * Print error
     *
     * @param response Render response object
     * @param error    Error label
     */
    private void printError(RenderResponse response, String error) {
        try {
            response.setContentType("text/html");
            String formattedError = "<div style='color:red;'> Error: " + error + "</div> <br/>";
            response.getPortletOutputStream().write(formattedError.getBytes());
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
