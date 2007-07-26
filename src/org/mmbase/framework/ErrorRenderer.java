/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import javax.servlet.http.*;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * If rendering of a block fails, for some reason, that this renderer can be used in stead, to
 * present the error.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ErrorRenderer.java,v 1.2 2007-07-26 23:35:50 michiel Exp $
 * @since MMBase-1.9
 */
public class ErrorRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(ErrorRenderer.class);

    protected final int statusCode;
    protected final String message;
    protected final String url;

    public ErrorRenderer(Type t, Block parent, String u, int status, String m) {
        super(t, parent);
        statusCode = status;
        message = m;
        url = u;
    }
    public Parameter[] getParameters() {
        return new Parameter[] {Parameter.RESPONSE, Parameter.REQUEST, Parameter.LOCALE};
    }

    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w, Renderer.WindowState state) throws FrameworkException {
        if (getType() == Type.BODY) {
            try {
                HttpServletRequest request   = blockParameters.get(Parameter.REQUEST);
                HttpServletResponse response = blockParameters.get(Parameter.RESPONSE);
                Locale  locale = blockParameters.get(Parameter.LOCALE);
                w.write("<div id=\"" + request.getAttribute("componentId") + "\"");
                w.write(" class=\"error mm_c_" + getBlock().getComponent().getName() + " mm_c_b_" + getBlock().getName() + " " + request.getAttribute("className") + "\">");
                w.write("<h1>" + statusCode + ": " + url + "</h1>");
                w.write("<p>" + message + "</p>");
                w.write("</div>");
            } catch (IOException eio) {
                throw new FrameworkException(eio.getMessage(), eio);
            }
        } else {
        }
    }
    public String toString() {
        return "ERROR " + statusCode + " " + message;
    }
    public java.net.URI getUri() {
        try {
            return new java.net.URL(url).toURI();
        } catch (Exception e) {
            return null;
        }
    }

}
