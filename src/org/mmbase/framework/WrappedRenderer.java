/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import org.mmbase.util.functions.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A generic implementation of a Renderer that wraps another Renderer. It defines and implments the
 * parameter 'wraps' which you can point to another block.
 *
 * @author Michiel Meeuwissen
 * @version $Id: WrappedRenderer.java,v 1.1 2009-01-10 18:32:33 michiel Exp $
 * @since MMBase-1.9.1
 */
public abstract class WrappedRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(WrappedRenderer.class);


    protected Renderer wrapped;

    public WrappedRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void setWraps(String c) {
        wrapped = getBlock().getComponent().getBlock(c).getRenderer(getType());
    }

    public Renderer getWraps() {
        return wrapped;
    }

    @Override public  Parameter[] getParameters() {
        return wrapped.getParameters();
    }



    public String toString() {
        return "wrapped " + wrapped;
    }

    public java.net.URI getUri() {
        return wrapped.getUri();
    }
}
