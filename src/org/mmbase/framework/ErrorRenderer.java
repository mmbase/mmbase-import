/*

  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.

  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.*;
import java.io.*;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * If rendering of a {@link Block} fails for some reason this renderer should be used to present the error.
 *
 * @todo Its workings are similar to /mmbase/errorpages/500.jsp, and we should consider that these two
 * share code.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public class ErrorRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(ErrorRenderer.class);

    protected final Error error;
    protected final String url;

    protected static int MAX_CAUSES = 4;

    public ErrorRenderer(Type t, Block parent, String u, int status, String m) {
        super(t, parent);
        error = new Error(status, new Exception(m));
        url = u;
    }

    public ErrorRenderer(Type t, Block parent, String u, int status, Throwable e) {
        super(t, parent);
        error = new Error(status, e);
        url = u;
    }

    @Override
    public  Parameter[] getParameters() {
        return new Parameter[] {Parameter.RESPONSE};
    }


    @Override
    public void render(Parameters blockParameters, Writer w, RenderHints hints) throws FrameworkException {
        log.debug("Error rendering " + blockParameters);
        switch(getType()) {
        case BODY:
            try {
                decorateIntro(hints, w, "error");
                w.write("<h1>" + error.status );
                w.write(": ");
                CharTransformer escape = new Xml(Xml.ESCAPE);
                w.write(escape.transform(error.exception.getMessage()));
                w.write(" ");
                w.write(escape.transform(url));
                w.write("</h1>");
                w.write("<pre>");
                HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
                error.getErrorReport(w, request, escape);
                w.write("</pre>");
                decorateOutro(hints, w);
            } catch (IOException eio) {
                throw new FrameworkException(eio.getMessage(), eio);
            }
            break;
        default:
        }
    }
    public String toString() {
        return "ERROR " + error;
    }

    public java.net.URI getUri() {
        try {
            return new java.net.URL(url).toURI();
        } catch (Exception e) {
            return null;
        }
    }
    public static class Error {
        public int status;
        public final Throwable exception;
        protected String title = null;

        public Error(int s, Throwable e) {
            status = s; exception = e;
        }


        protected LinkedList<Throwable> getStack() {
            Throwable e = exception;
            LinkedList<Throwable> stack = new LinkedList<Throwable>();
            while (e != null) {
                stack.addFirst(e);
                if (e instanceof NotFoundException) {
                    status = HttpServletResponse.SC_NOT_FOUND;
                }
                if (e instanceof ServletException) {
                    Throwable t = ((ServletException) e).getRootCause();
                    if (t == null) t = e.getCause();
                    e = t;
                } else if (e instanceof javax.servlet.jsp.JspException) {
                    Throwable t = ((JspException) e).getRootCause();
                    if (t == null) t = e.getCause();
                    e = t;
                } else {
                    e = e.getCause();
                }
            }
            return stack;
        }

        protected String getTitle(Throwable t) {
            String message = t.getMessage();
            String title = message;
            if (title == null) {
                StackTraceElement el = t.getStackTrace()[0];
                title = t.getClass().getName().substring(t.getClass().getPackage().getName().length() + 1) + " " + el.getFileName() + ":" + el.getLineNumber();
            }
            return title;
        }

        public String getTitle() {
            LinkedList<Throwable> stack = getStack();
            if (stack.isEmpty()) {
                return "NO EXCEPTION";
            } else {
                return getTitle(stack.removeFirst());
            }
        }



        public Writer getErrorReport(Writer to, final HttpServletRequest request, CharTransformer escape) throws IOException {
            final Writer logMsg = new StringWriter();
            final Writer tee    = new org.mmbase.util.ChainedWriter(to, logMsg);
            Writer msg = tee;

            LinkedList<Throwable> stack = getStack();
            String ticket = new Date().toString();


            if (request != null) {
                msg.append("Headers\n----------\n");
                // request properties
                Enumeration en = request.getHeaderNames();
                while (en.hasMoreElements()) {
                    String name = (String) en.nextElement();
                    msg.append(escape.transform(name + ": "+ escape.transform(request.getHeader(name)) + "\n"));
                }

                msg.append("\nAttributes\n----------\n");
                Enumeration en2 = request.getAttributeNames();
                while (en2.hasMoreElements()) {
                    String name = (String) en2.nextElement();
                    msg.append(escape.transform(name + ": " + request.getAttribute(name) + "\n"));
                }
            }
            msg.append("\n");
            msg.append("Misc. properties\n----------\n");

            if (request != null) {
                msg.append("method: ").append(escape.transform(request.getMethod())).append("\n");
                msg.append("querystring: ").append(escape.transform(request.getQueryString())).append("\n");
                msg.append("requesturl: ").append(escape.transform(request.getRequestURL().toString())).append("\n");
            }

            msg.append("mmbase version: ").append(org.mmbase.Version.get()).append("\n");
            msg.append("status: ").append("" + status).append("\n\n");


            if (request != null) {
                msg.append("Parameters\n----------\n");
                // request parameters
                Enumeration en = request.getParameterNames();
                while (en.hasMoreElements()) {
                    String name = (String) en.nextElement();
                    msg.append(name).append(": ").append(escape.transform(request.getParameter(name))).append("\n");
                }
            }
            msg.append("\nException " + ticket + "\n----------\n\n" + (exception != null ? (escape.transform(exception.getClass().getName())) : "NO EXCEPTION") + ": ");

            int wroteCauses = 0;
            while (! stack.isEmpty()) {

                Throwable t = stack.removeFirst();
                // add stack stacktraces
                if (t != null) {
                    if (stack.isEmpty()) { // write last message always
                        msg = tee;
                    }
                    String message = t.getMessage();
                    if (msg != tee) {
                        to.append("\n=== skipped(see log)  : " + escape.transform(t.getClass().getName()) + ": " + message + "\n");
                    }

                    msg.append("\n\n").append(escape.transform(t.getClass().getName() + ": " + message));
                    StackTraceElement[] stackTrace = t.getStackTrace();
                    for (StackTraceElement e : stackTrace) {
                        msg.append("\n        at ").append(escape.transform(e.toString()));
                    }
                    if (! stack.isEmpty()) {
                        msg.append("\n-------caused:\n");
                    }
                    wroteCauses++;
                    if (wroteCauses >= MAX_CAUSES ) {
                        msg = logMsg;
                    }
                }
            }
            // write errors to mmbase log
            if (status == 500) {
                log.error("TICKET " + ticket + ":\n" + logMsg);
            }
            return to;
        }
    }

}
