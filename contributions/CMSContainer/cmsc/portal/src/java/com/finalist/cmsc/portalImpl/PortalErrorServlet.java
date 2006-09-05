/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.commons.util.HttpUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import com.finalist.pluto.portalImpl.core.*;
import com.finalist.util.version.VersionUtil;

@SuppressWarnings("serial")
public class PortalErrorServlet extends PortalServlet {

    private static Log log = LogFactory.getLog(PortalErrorServlet.class);

    /** the date + time long format */
    private static final SimpleDateFormat DATE_TIME_FORMAT =
       new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

    protected ServletConfig config;

    protected static final String[] vars = {
        ERROR_STATUS_CODE,
        ERROR_EXCEPTION_TYPE,
        ERROR_MESSAGE,
        ERROR_EXCEPTION,
        ERROR_REQUEST_URI
    };
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        // do not start the portal
        this.config = config;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("===>PortalErrorServlet.doGet START!");

        if (PortletContainerFactory.getPortletContainer().isInitialized()) {
            PortalRegistry reg = PortalRegistry.getPortalRegistry(request);
            PortalEnvironment env = new PortalEnvironment(request, response, config);
            PortalURL currentURL = env.getRequestedPortalURL();
            try {
                String path = extractPath(request, currentURL);
                Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
                log.debug("===>getErrorScreen:'" + path + "'");
                ScreenFragment screen = null;
                Site site = SiteManagement.getSiteFromPath(path);
                if (site != null && SiteManagement.isNavigation(site.getUrlfragment() + PATH_SP + statusCode)) {
                    screen = getScreen(site.getUrlfragment() + PATH_SP + statusCode);
                }
                if (screen == null) {
                    List<Site> sites = SiteManagement.getSites();
                    if (!sites.isEmpty() && SiteManagement.isNavigation(sites.get(0).getUrlfragment() + PATH_SP + statusCode)) {
                        screen = getScreen(sites.get(0).getUrlfragment() + PATH_SP + statusCode);
                    }
                }
                if (screen != null) {
                    try {
                        logError(request);
                        Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
                        CloudUtil.addCloudToThread(cloud);
                        response.setContentType(CONTENT_TYPE);
                        reg.setScreen(screen);
                        log.debug("===>SERVICE");
                        screen.service(request, response);
                        log.debug("===>SERVICE DONE");
                    }
                    finally {
                        CloudUtil.removeCloudFromThread();
                    }
                } 
                else {
                    RequestDispatcher rd = config.getServletContext().getRequestDispatcher("/error/" + statusCode + ".jsp");
                    if (rd != null) {
                        rd.forward(request, response);
                    }
                    else {
                        logError(request);
                        basicErrorPage(request, response);
                    }
                }
            } catch (Throwable t) {
                log.fatal("Error processing", t);
            }
        }
        else {
            basicErrorPage(request, response);
        }
        log.debug("===>PortalErrorServlet.doGet EXIT!");
    }

    private void basicErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>" + request.getAttribute(ERROR_STATUS_CODE)
                + " " + request.getAttribute(ERROR_MESSAGE) + "</title></head><body><table>"); 
        // Print vars
        for (int i = 0; i < vars.length; i++) {
            out.println(
                "<tr><td>" + vars[i] + "</td><td>" +
                request.getAttribute(vars[i]) + 
                "</td></tr>");
        }
        out.println("</table></body></html>");
    }

    /**
     * Creates String.from given long according to dd-MM-yyyy HH:mm:ss
     * @param date the date to format
     * @return Datestring
     */
    public static String getDateTimeString(long date) {
       return DATE_TIME_FORMAT.format(new Date(date));
    }
    
    public void logError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
        Throwable exception = (Throwable) request.getAttribute(ERROR_EXCEPTION);
        if (statusCode == 500) {
            String version = VersionUtil.getVersion(config.getServletContext());
            // prepare error ticket
            long ticket = System.currentTimeMillis();
        
            String msg = HttpUtil.getErrorInfo(request, exception, ticket, version);
            
            String message = "";
            if (exception != null) {
                message = exception.getMessage();
                if (message == null) {
                    message = exception.toString();
                }
            }
            // write errors to mmbase log
            log.error(ticket+":\n" + msg);
        }
    }
}
