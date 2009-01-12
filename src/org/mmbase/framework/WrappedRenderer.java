/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import org.mmbase.util.functions.*;
import java.io.Writer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A generic implementation of a Renderer that wraps another Renderer. It defines and implments the
 * parameter 'wraps' which you can point to another block.
 *
 * @author Michiel Meeuwissen
 * @version $Id: WrappedRenderer.java,v 1.3 2009-01-12 22:12:04 michiel Exp $
 * @since MMBase-1.9.1
 */
public abstract class WrappedRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(WrappedRenderer.class);


    protected Renderer wrapped;

    public WrappedRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void setWrapsBlock(String c) {
        setWraps(getBlock().getComponent().getBlock(c).getRenderer(getType()));
    }

    public void setWraps(Renderer r) {
        wrapped = r;
    }

    public Renderer getWraps() {
        return wrapped;
    }

    @Override public  Parameter[] getParameters() {
        return wrapped.getParameters();
    }

    @Override public void render(Parameters blockParameters, Writer w, RenderHints hints) throws FrameworkException {
        wrapped.render(blockParameters, w, hints);
    }

    @Override public String toString() {
        return "wrapped " + wrapped;
    }

    @Override public java.net.URI getUri() {
        return wrapped.getUri();
    }
}
