/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.Collection;
import org.mmbase.util.Casting;
import org.mmbase.bridge.*;

/**
 * The Node data type describes a data type which is based on an MMBase 'node' field. So the value
 * is an MMBase node, which can normally be described by a foreign key.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: NodeDataType.java,v 1.23 2006-01-23 12:16:47 pierre Exp $
 * @since MMBase-1.8
 */
public class NodeDataType extends BasicDataType {

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    protected MustExistRestriction mustExistRestriction = new MustExistRestriction();

    /**
     * Constructor for node field.
     */
    public NodeDataType(String name) {
        super(name, Node.class);
    }


    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof NodeDataType) {
            mustExistRestriction.inherit(((NodeDataType)origin).mustExistRestriction);
        }
    }
    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof NodeDataType) {
            mustExistRestriction = new MustExistRestriction(((NodeDataType)origin).mustExistRestriction);
        }
    }

    /**
     * Casting to Node can be a expensive, so we cast to integer in stead.
     */
    protected Object cast(Object value, Cloud cloud, Node node, Field field) {
        Object preCast = preCast(value, cloud, node, field);
        if (preCast == null) {
            return null;
        } else if (preCast instanceof Node || preCast instanceof Integer) {
            return preCast;
        } else {
            return Casting.toType(Integer.class, cloud, preCast);
        }
    }

    protected boolean isCorrectType(Object value) {
        return super.isCorrectType(value) || Casting.isType(Integer.class, value);
    }

    /**
     * Whether the Node of the value must exist
     *
     * XXX MM: How can you have a non-existing node? I don't really get it. AFAIK all nodes exist.
     *              especially since a node field is essentially a foreign key.
     */
    public boolean mustExist() {
        return mustExistRestriction.getValue().equals(Boolean.TRUE);
    }

    public MustExistRestriction getMustExistRestriction() {
        mustExistRestriction.setFixed(true);
        return mustExistRestriction;
    }

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, node, field);
        errors = mustExistRestriction.validate(errors, castedValue, node, field);
        return errors;
    }

    private class MustExistRestriction extends AbstractRestriction {
        MustExistRestriction(MustExistRestriction me) {
            super(me);
            enforceStrength = DataType.ENFORCE_ONCHANGE;
        }
        MustExistRestriction() {
            super("mustExist", Boolean.TRUE);
            enforceStrength = DataType.ENFORCE_ONCHANGE;
        }
        protected boolean simpleValid(Object v, Node node, Field field) {
            if (getValue().equals(Boolean.TRUE)) {
                if (v != null) {
                    if (v instanceof String) {
                        return node.getCloud().hasNode((String)v);
                    } else if (v instanceof Number) {
                        int num = ((Number)v).intValue();
                        if (num < 0) return true;
                        return node.getCloud().hasNode(num);
                    } else if (v instanceof Node) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
