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

/**
 * A Processor implmentation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JspProcessor.java,v 1.1 2006-10-13 12:20:50 johannes Exp $
 * @since MMBase-1.9
 */
public class JspProcessor extends AbstractProcessor {

    public static Parameter ESSENTIAL = new Parameter.Wrapper(Parameter.RESPONSE, Parameter.REQUEST);

    protected final String path;

    public JspProcessor(String p) {
        super();
        path = p;
    }

    public Parameters createParameters() {
        return new Parameters(ESSENTIAL, getSpecificParameters()); 
    }

    public void process(Parameters parameters) throws IOException {
        try {
            HttpServletResponse response = parameters.get(Parameter.RESPONSE);
            GenericResponseWrapper respw = new GenericResponseWrapper(response);
            HttpServletRequest request = parameters.get(Parameter.REQUEST);
            for (Map.Entry<String, ?> entry : parameters.toMap().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
            requestDispatcher.include(request, respw);
        } catch (ServletException se) {
            IOException e =  new IOException(se.getMessage());
            e.initCause(se);
            throw e;
        }
    }
}
