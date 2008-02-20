/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Abstract renderer implementation which implements getType and getBlock.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractRenderer.java,v 1.10 2008-02-20 17:44:07 michiel Exp $
 * @since MMBase-1.9
 */
abstract public class AbstractRenderer implements Renderer {

    private static final Logger log = Logging.getLoggerInstance(AbstractRenderer.class);

    protected final Type type;
    private final Block parent;

    public AbstractRenderer(Type t, Block p) {
        type = t;
        parent = p;
    }
    public AbstractRenderer(String t, Block p) {
        type = Type.valueOf(t);
        parent = p;
    }

    public Type getType() {
        return type;
    }

    public Block getBlock() {
        return parent;
    }

    public java.net.URI getUri() {
        return null;
    }
}
