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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * HEAD renderers can be chained, because they don't produce a well defined block.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ChainedRenderer.java,v 1.2 2007-12-26 17:07:19 michiel Exp $
 * @since MMBase-1.9
 */

public class ChainedRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(ChainedRenderer.class);

    protected final List<Renderer> chain = new ArrayList<Renderer>();
    protected Parameter[] parameters = Parameter.EMPTY;

    public ChainedRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void add(Renderer render) {
        chain.add(render);
        List<Parameter> params = new ArrayList<Parameter>(Arrays.asList(parameters));
        params.addAll(Arrays.asList(render.getParameters()));
        parameters = params.toArray(Parameter.EMPTY);
    }

    public  Parameter[] getParameters() {
        return parameters;
    }


    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w, WindowState state) throws FrameworkException {
        for (Renderer renderer : chain) {
            renderer.render(blockParameters, frameworkParameters, w, state);
        }
    }

    public String toString() {
        return chain.toString();
    }

    public java.net.URI getUri() {
        if (chain.size() > 0) {
            return chain.get(0).getUri();
        } else {
            return null;
        }
    }
}
