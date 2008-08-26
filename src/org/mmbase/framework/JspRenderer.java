/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.GenericResponseWrapper;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Renderer implementation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JspRenderer.java,v 1.28 2008-08-26 06:45:36 michiel Exp $
 * @since MMBase-1.9
 */
public class JspRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(JspRenderer.class);

    public static String JSP_ROOT = "/mmbase/components/";

    protected final String path;

    public JspRenderer(String t, String p, Block parent) {
        super(t, parent);
        path = p;
    }

    public String getPath() {
        return path.charAt(0) == '/' ? path : JSP_ROOT + getBlock().getComponent().getName() + '/' + path;
    }

    @Override
    public  Parameter[] getParameters() {
        return new Parameter[] {Parameter.RESPONSE, Parameter.REQUEST};
    }

    private static class Status {
        public int code = 200;
        public String mesg = null;
    }
    @Override
    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w, RenderHints hints) throws FrameworkException {
        try {
            HttpServletResponse response = blockParameters.get(Parameter.RESPONSE);
            HttpServletRequest request  = blockParameters.get(Parameter.REQUEST);
            final Status status = new Status();
            GenericResponseWrapper respw = new GenericResponseWrapper(response) {
                    public void setStatus(int s) {
                        status.code = s;
                    }
                    public void sendError(int s) throws IOException {
                        status.code = s;
                    }
                    public void sendError(int s, String m) throws IOException {
                        status.code = s;
                        status.mesg = m;
                    }
                };
            //String url = getFramework().getInternalUrl(getPath(), this, getBlock().getComponent(), blockParameters, frameworkParameters).toString();

            String url = getPath();
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);

            for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            if (log.isDebugEnabled()) {
                log.debug("Using block.parameters " +  blockParameters.toMap());
                log.debug("Rendering " + url + " status " + status.code);
            }
            requestDispatcher.include(request, respw);
            log.debug("Status " + status.code);
            if (status.code == 401) {
                DeniedRenderer denied = new DeniedRenderer(getType(), getBlock());
                denied.render(blockParameters, frameworkParameters, w, hints);
            } else if (status.code != 200) {
                ErrorRenderer error = new ErrorRenderer(getType(), getBlock(), url, status.code, status.mesg);
                error.render(blockParameters, frameworkParameters, w, hints);
            } else {
                w.write(respw.toString());
            }
        } catch (ServletException se) {
            throw new FrameworkException(se.getMessage(), se);
        } catch (IOException ioe) {
            throw new FrameworkException(ioe.getMessage(), ioe);
        }
    }

    public String toString() {
        Parameter.Wrapper wrapper = getBlock().specific;
        return getPath() + (wrapper == null ? "" : "?" + wrapper);
    }

    @Override
    public java.net.URI getUri() {
        try {
            return org.mmbase.util.ResourceLoader.getWebRoot().getResource(getPath()).toURI();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
