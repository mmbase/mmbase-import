/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.Arrays;
import org.mmbase.util.functions.*;
import org.mmbase.util.GenericResponseWrapper;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Processor implementation based on a jsp in the /mmbase/components/ directory.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
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
        return path.charAt(0) == '/' ? path : JspRenderer.JSP_ROOT + getBlock().getComponent().getName() + '/' + path;
    }

    public Parameter<?>[] getParameters() {
        return new Parameter<?>[] {Parameter.RESPONSE, Parameter.REQUEST};
    }



    public void process(Parameters blockParameters) throws FrameworkException {
        try {
            HttpServletResponse response = blockParameters.get(Parameter.RESPONSE);
            GenericResponseWrapper respw = new GenericResponseWrapper(response);
            HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
            Framework framework = Framework.getInstance();
            // String url = framework.getInternalUrl(getPath(), this, parent.getComponent(), blockParameters.toEntryList(), frameworkParameters).toString();
            String url = getPath();
            if (log.isDebugEnabled()) {
                log.debug("Block parameters      : [" + blockParameters + "]");
                log.debug("Framework returned url: [" + url + "]");
            }
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
            requestDispatcher.include(request, respw);
        } catch (ServletException se) {
            throw new FrameworkException(se.getMessage(), se);
        } catch (IOException ioe) {
            throw new FrameworkException(ioe.getMessage(), ioe);
        }
    }

    @Override
    public String toString() {
        return getPath() + '?' + Arrays.asList(getParameters());
    }

    @Override
    public java.net.URI getUri() {
        try {
            return org.mmbase.util.ResourceLoader.getWebRoot().getResource(getPath()).toURI();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

}
