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
import java.lang.reflect.Method;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The overly simple renderer which is simply based on a String present in the configuration file
 *
 * @author Michiel Meeuwissen
 * @version $Id: StringRenderer.java,v 1.3 2006-11-07 18:55:03 michiel Exp $
 * @since MMBase-1.9
 */
public class StringRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(StringRenderer.class);

    protected String string;
    protected Map<String, Method> requestMethods;

    public StringRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void setString(String s) {
        string = s;
        requestMethods = PatternNodeFunctionProvider.getRequestMethods(string); 
    }

    public  Parameter[] getParameters() {
        return new Parameter[] {Parameter.REQUEST};
    }

    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException {
        HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
        if (request == null) throw new RuntimeException("No request parameter in " + blockParameters);
        Object previousRenderer = request.getAttribute(Renderer.KEY);
        StringBuffer sb = new StringBuffer(string);
        PatternNodeFunctionProvider.handleRequest(sb, blockParameters, requestMethods);
        w.write(sb.toString());
        request.setAttribute(Renderer.KEY, previousRenderer);
    }

    public String toString() {
        return string;
    }
}
