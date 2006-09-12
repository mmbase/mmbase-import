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
 * A View implmentation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JspView.java,v 1.1 2006-09-12 19:25:59 michiel Exp $
 * @since MMBase-1.9
 */
public class JspView extends AbstractView {

    public static Parameter ESSENTIAL = new Parameter.Wrapper(Parameter.RESPONSE, Parameter.REQUEST);

    protected final String path;

    public JspView(String t, String p) {
        super(t);
        path = p;
    }

    public Parameters createParameters() {
        return new Parameters(ESSENTIAL, getSpecificParameters()); 
    }
    public void render(Parameters parameters, Writer w) throws IOException {
        try {
            HttpServletResponse response = (HttpServletResponse) parameters.get(Parameter.RESPONSE);
            GenericResponseWrapper respw = new GenericResponseWrapper(response);
            HttpServletRequest request = (HttpServletRequest) parameters.get(Parameter.REQUEST);
            Set<Map.Entry<String, ?>> entries = parameters.toMap().entrySet();
            for (Map.Entry<String, ?> entry : entries) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
            requestDispatcher.include(request, respw);
            w.write(respw.toString());
        } catch (ServletException se) {
            IOException e =  new IOException(se.getMessage());
            e.initCause(se);
            throw e;
        }
    }


}
