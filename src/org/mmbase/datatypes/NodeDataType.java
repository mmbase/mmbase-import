/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.Collection;
import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: NodeDataType.java,v 1.9 2005-09-06 21:11:30 michiel Exp $
 * @since MMBase-1.8
 */
public class NodeDataType extends DataType {

    protected final DataType.ValueConstraint mustExistConstraint = new MustExistConstraint();

    /**
     * Constructor for node field.
     */
    public NodeDataType(String name) {
        super(name, MMObjectNode.class);
    }

    public void erase() {
        super.erase();
        if (mustExistConstraint != null) { // check because erase is called from constructor of super too, and so member can still be null.
            mustExistConstraint.setFixed(false);
            mustExistConstraint.setValue(Boolean.TRUE);
        }
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof NodeDataType) {
            mustExistConstraint.inherit(((NodeDataType)origin).mustExistConstraint);
        }
    }

    public boolean mustExist() {
        return mustExistConstraint.getValue().equals(Boolean.TRUE);
    }

    public DataType.ValueConstraint getMustExistConstraint() {
        mustExistConstraint.setFixed(true);
        return mustExistConstraint;
    }

    public Collection validate(Object value, Node node, Field field) {
        Collection errors = super.validate(value, node, field);
        errors = mustExistConstraint.validate(errors, value, node, field);
        return errors;
    }

    private class MustExistConstraint extends ValueConstraint {
        MustExistConstraint() {
            super("mustExist", Boolean.TRUE);
        }
        public boolean valid(Object value, Node node, Field field) {
            if (getValue().equals(Boolean.TRUE)) {
                if (value != null && !(value instanceof Number && ((Number)value).intValue() == -1)) {
                    MMObjectNode nodeValue = Casting.toNode(value, MMBase.getMMBase().getTypeDef());
                    if (nodeValue == null) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
