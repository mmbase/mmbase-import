/*

  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.

  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import java.util.regex.*;

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
    public  Parameter<?>[] getParameters() {
        return new Parameter<?>[] {Parameter.RESPONSE};
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
    @Override
    public String toString() {
        return "ERROR " + error;
    }

    @Override
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
        private Boolean showSession = null;
        private Pattern requestIgnore = null;
        private Pattern sessionIgnore = null;
        private Boolean showMMBaseVersion = null;

        public Error(int s, Throwable e) {
            status = s; exception = e;
        }


        public void setShowSession(Boolean b) {
            showSession = b;
        }
        public void setRequestIgnore(String i) {
            requestIgnore = i == null ? null : Pattern.compile(i);
        }
        public void setSessionIgnore(String i) {
            sessionIgnore = i == null ? null : Pattern.compile(i);
        }
        public void setShowVersion(Boolean b) {
            showMMBaseVersion = b;
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
            String tit = message;
            if (tit == null) {
                StackTraceElement el = t.getStackTrace()[0];
                tit = t.getClass().getName().substring(t.getClass().getPackage().getName().length() + 1) + " " + el.getFileName() + ":" + el.getLineNumber();
            }
            return tit;
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

            Map<String, String> props;
            try {
                props = org.mmbase.util.ApplicationContextReader.getProperties("mmbase_errorpage");
            } catch (javax.naming.NamingException ne) {
                props = Collections.emptyMap();
                log.info(ne);
            }

            if (request != null) {
                {
                    msg.append("Headers\n----------\n");
                    // request properties
                    for (Object name : Collections.list(request.getHeaderNames())) {
                        msg.append(escape.transform(name + ": "+ escape.transform(request.getHeader((String) name)) + "\n"));
                    }
                }
                {
                    msg.append("\nAttributes\n----------\n");
                    Pattern p = requestIgnore;
                    if (p == null && props.get("request_ignore") != null) {
                        p = Pattern.compile(props.get("request_ignore"));
                    }
                    for (Object name : Collections.list(request.getAttributeNames())) {
                        if (p == null || !p.matcher((String) name).matches()) {
                            msg.append(escape.transform(name + ": " + request.getAttribute((String) name) + "\n"));
                        }
                    }
                }
                if (Boolean.TRUE.equals(showSession)  || (showSession == null && ! "false".equals(props.get("show_session")))) {
                    HttpSession ses = request.getSession(false);
                    if (ses != null) {
                        msg.append("\nSession\n----------\n");
                        Pattern p = sessionIgnore;
                        if (p == null && props.get("session_ignore") != null) {
                            p = Pattern.compile(props.get("session_ignore"));
                        }
                        for (Object name : Collections.list(ses.getAttributeNames())) {
                            if (p == null || !p.matcher((String) name).matches()) {
                                msg.append(escape.transform(name + ": " + ses.getAttribute((String) name) + "\n"));
                            }
                        }

                    }
                }
            }
            msg.append("\n");
            msg.append("Misc. properties\n----------\n");

            if (request != null) {
                msg.append("method: ").append(escape.transform(request.getMethod())).append("\n");
                msg.append("querystring: ").append(escape.transform(request.getQueryString())).append("\n");
                msg.append("requesturl: ").append(escape.transform(request.getRequestURL().toString())).append("\n");
            }
            if (Boolean.TRUE.equals(showMMBaseVersion)  || (showMMBaseVersion == null && ! "false".equals(props.get("show_mmbase_version")))) {
                msg.append("mmbase version: ").append(org.mmbase.Version.get()).append("\n");
            }
            msg.append("status: ").append("").append(String.valueOf(status)).append("\n\n");


            if (request != null) {
                msg.append("Parameters\n----------\n");
                // request parameters
                Enumeration en = request.getParameterNames();
                while (en.hasMoreElements()) {
                    String name = (String) en.nextElement();
                    msg.append(name).append(": ").append(escape.transform(request.getParameter(name))).append("\n");
                }
            }
            msg.append("\nException ").append(ticket).append("\n----------\n\n").append(exception != null ? (escape.transform(exception.getClass().getName())) : "NO EXCEPTION").append(": ");

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
                        to.append("\n=== skipped(see log)  : ").append(escape.transform(t.getClass().getName())).append(": ").append(message).append("\n");
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
            // write errors to  log
            if (status == 500) {
                try {
                    if (props.get("to") != null && props.get("to").length() > 0) {
                        javax.naming.Context initCtx = new javax.naming.InitialContext();
                        javax.naming.Context envCtx = (javax.naming.Context)initCtx.lookup("java:comp/env");
                        Object mailSession = envCtx.lookup("mail/Session");
                        Class sessionClass = Class.forName("javax.mail.Session");
                        Class recipientTypeClass = Class.forName("javax.mail.Message$RecipientType");
                        Class messageClass = Class.forName("javax.mail.internet.MimeMessage");
                        Object mail = messageClass.getConstructor( sessionClass).newInstance(mailSession);
                        messageClass.getMethod("addRecipients", recipientTypeClass, String.class).invoke(mail, recipientTypeClass.getDeclaredField("TO").get(null), props.get("to"));
                        messageClass.getMethod("setSubject", String.class).invoke(mail, ticket);
                        mail.getClass().getMethod("setText", String.class).invoke(mail, logMsg.toString());
                        Class.forName("javax.mail.Transport").getMethod("send", Class.forName("javax.mail.Message")).invoke(null, mail);
                        tee.append("\nmailed to (").append(String.valueOf(props)).append(")");
                    }

                } catch (Exception nnfe) {
                    tee.append("\nnot mailed (").append(String.valueOf(nnfe)).append(")");
                    if (log.isDebugEnabled()) {
                        log.debug(nnfe.getMessage(), nnfe);
                    }
                }
                log.error("TICKET " + ticket + ":\n" + logMsg);
            }
            return to;
        }
    }

}
