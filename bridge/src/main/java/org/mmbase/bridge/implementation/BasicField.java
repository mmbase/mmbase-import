/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.FieldWrapper;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicField extends FieldWrapper implements Field {

    private static final Logger log = Logging.getLoggerInstance(BasicField.class);

    private final NodeManager nodeManager;


    public BasicField(Field field, NodeManager nodeManager) {
        super(field);
        this.nodeManager = nodeManager;
    }

    @Override
    public NodeManager getNodeManager() {
        return nodeManager;
    }

    @Override
    public int compareTo(Field f) {
       int compared = getName().compareTo(f.getName());
       if (compared == 0) compared = getDataType().compareTo(f.getDataType());
       return compared;
   }
}
