/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.datatypes.handlers.Request;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.GenericResponseWrapper;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.io.*;


/**
 * This utility class helps you to implement HTML handlers using JSP/Taglib. This supposes request and response objects
 * as properties on the {@link Request} properties. (The MMBase Taglib will do that).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.6
 */

public class Jsp {

    private final HttpServletResponse response ;
    private final HttpServletRequest request;

    /**
     * Instantiates.
     * @throws IllegalStateException if there is no response and/or no request object in the Request object
     */
    public Jsp(Request r) {
        response = r.getProperty(Parameter.RESPONSE);
        if (response == null) throw new IllegalStateException("No Http response found in " + r);
        request  = r.getProperty(Parameter.REQUEST);
        if (request == null) throw new IllegalStateException("No Http response found in " + r);

    }

    public void render(String jsp, Map<String, Object> arguments, Writer w) throws IOException, ServletException {
        GenericResponseWrapper respw = new GenericResponseWrapper(response);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(jsp);
        for (Map.Entry<String, ?> entry : arguments.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        requestDispatcher.include(request, respw);
        w.write(respw.toString());
    }
}
