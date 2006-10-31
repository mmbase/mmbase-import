/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Abstract view implementation which implements getType and creates the 'essential' parameters
 * request and response.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractRenderer.java,v 1.4 2006-10-31 22:21:45 michiel Exp $
 * @since MMBase-1.9
 */
abstract public class AbstractRenderer implements Renderer {

    private static final Logger log = Logging.getLoggerInstance(AbstractRenderer.class);

    protected final Type type;
    private final Block parent;

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


}
