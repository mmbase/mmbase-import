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

/**
 * A Renderer implmentation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JspRenderer.java,v 1.3 2006-10-14 09:43:59 johannes Exp $
 * @since MMBase-1.9
 */
public class JspRenderer extends AbstractRenderer {

    public static Parameter ESSENTIAL = new Parameter.Wrapper(Parameter.RESPONSE, Parameter.REQUEST);

    protected final String path;
    private final Block parent;

    public JspRenderer(String t, String p, Block parent) {
        super(t);
        path = p;
        this.parent = parent;
    }

    public Block getBlock() {
        return parent;
    }

    public Parameters createParameters() {
        return new Parameters(ESSENTIAL, getSpecificParameters()); 
    }

    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException {
        try {
            HttpServletResponse response = blockParameters.get(Parameter.RESPONSE);
            GenericResponseWrapper respw = new GenericResponseWrapper(response);
            HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
            for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            Framework framework = MMBase.getMMBase().getFramework();
            String url = framework.getUrl(path, parent.getComponent(), blockParameters, frameworkParameters);

            RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
            requestDispatcher.include(request, respw);
            w.write(respw.toString());
        } catch (ServletException se) {
            IOException e =  new IOException(se.getMessage());
            e.initCause(se);
            throw e;
        }
    }
}
