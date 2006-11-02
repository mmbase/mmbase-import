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
import org.mmbase.module.core.MMBase;
import org.mmbase.util.functions.*;
import org.mmbase.util.GenericResponseWrapper;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Processor implementation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JspProcessor.java,v 1.5 2006-11-02 10:49:56 michiel Exp $
 * @since MMBase-1.9
 */
public class JspProcessor extends AbstractProcessor {
    private static final Logger log = Logging.getLoggerInstance(JspProcessor.class);

    protected final String path;

    public JspProcessor(String p, Block parent) {
        super(parent);
        path = p;
    }

    public String getPath() {
        return path;
    }

    public Parameter[] getParameters() {
        return new Parameter[] {Parameter.RESPONSE, Parameter.REQUEST};
    }



    public void process(Parameters blockParameters, Parameters frameworkParameters) throws IOException {
        try {
            HttpServletResponse response = blockParameters.get(Parameter.RESPONSE);
            GenericResponseWrapper respw = new GenericResponseWrapper(response);
            HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
            for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            Framework framework = MMBase.getMMBase().getFramework();
            String url = framework.getUrl(path, this, parent.getComponent(), blockParameters, frameworkParameters).toString();

            if (log.isDebugEnabled()) {
                log.debug("Block parameters      : [" + blockParameters + "]");
                log.debug("Framework parameters  : [" + frameworkParameters + "]");
                log.debug("Framework returned url: [" + url + "]");
            }

            RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
            requestDispatcher.include(request, respw);
        } catch (ServletException se) {
            IOException e =  new IOException(se.getMessage());
            e.initCause(se);
            throw e;
        }
    }
}
