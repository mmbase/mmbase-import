/*
f
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import javax.servlet.http.*;
import java.lang.reflect.Method;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The overly simple renderer which is simply based on a String present in the configuration
 * file. Based on {@link PatternNodeFunctionProvider} so several parameters can be present.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
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

    /**
     * @todo should consider the string
     */
    @Override
    public  Parameter[] getParameters() {
        return new Parameter[] {Parameter.REQUEST};
    }

    public void render(Parameters blockParameters, Writer w, RenderHints hints) throws FrameworkException {
        log.debug("Rendering " + string);
        try {
            HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
            if (request == null) throw new RuntimeException("No request parameter in " + blockParameters); // hmm, not be essential
            StringBuffer sb = new StringBuffer(string);
            PatternNodeFunctionProvider.handleRequest(sb, blockParameters, requestMethods);
            w.write(sb.toString());
        } catch (IOException ioe) {
            throw new FrameworkException(ioe.getMessage(), ioe);
        }
    }

    public String toString() {
        return string;
    }
}
