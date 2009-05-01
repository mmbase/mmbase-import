/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.CloneUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A function on nodes to clone them.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class CloneNodeFunction extends NodeFunction<Node> {

    private static final Logger log = Logging.getLoggerInstance(CloneNodeFunction.class);

    public static final Parameter<?>[] PARAMETERS = {
        new Parameter<Boolean>("relations", Boolean.class, Boolean.FALSE)
    };


    public CloneNodeFunction() {
        super("clone", PARAMETERS);
    }

    @Override protected Node getFunctionValue(Node node, Parameters parameters) {
        if (Boolean.TRUE.equals(parameters.get("relations"))) {
            return CloneUtil.cloneNodeWithRelations(node);
        } else {
            return CloneUtil.cloneNode(node);
        }
    }

}
