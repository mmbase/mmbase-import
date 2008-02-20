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
 * Renderer which can be used in stead, if rendering of a certain block proved to be not allowed for
 * the current user.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DeniedRenderer.java,v 1.3 2008-02-20 17:44:07 michiel Exp $
 * @since MMBase-1.9
 */

public class DeniedRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(DeniedRenderer.class);


    public DeniedRenderer(Type t, Block parent) {
        super(t, parent);
    }

    public Parameter[] getParameters() {
        return new Parameter[] {Parameter.RESPONSE, Parameter.REQUEST, Parameter.LOCALE};
    }

    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w, Renderer.WindowState state) throws FrameworkException {
        switch(getType()) {
        case BODY:
            try {
                HttpServletRequest request   = blockParameters.get(Parameter.REQUEST);
                HttpServletResponse response = blockParameters.get(Parameter.RESPONSE);
                Locale  locale = blockParameters.get(Parameter.LOCALE);
                w.write("<div id=\"" + request.getAttribute(Framework.COMPONENT_ID_KEY) + "\"");
                w.write(" class=\"denied mm_c_");
                w.write(getBlock().getComponent().getName());
                w.write(" mm_c_b_");
                w.write(getBlock().getName());
                w.write(" " + request.getAttribute(Framework.COMPONENT_CLASS_KEY));
                w.write("\">");
                w.write("<h1>Denied</h1>");
                w.write("</div>");

            } catch (IOException eio) {
                throw new FrameworkException(eio.getMessage(), eio);
            }
            break;
        default:
        }
    }
    public String toString() {
        return "DENIED";
    }

}
