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
 * A Processor implmentation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JspProcessor.java,v 1.4 2006-10-25 20:28:23 michiel Exp $
 * @since MMBase-1.9
 */
public class JspProcessor extends AbstractProcessor {
    private static final Logger log = Logging.getLoggerInstance(JspProcessor.class);

    public static Parameter ESSENTIAL = new Parameter.Wrapper(Parameter.RESPONSE, Parameter.REQUEST);

    protected final String path;
    private final Block parent;

    public JspProcessor(String p, Block parent) {
        super();
        path = p;
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }


    public Parameters createParameters() {
        return new Parameters(ESSENTIAL, getSpecificParameters()); 
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
