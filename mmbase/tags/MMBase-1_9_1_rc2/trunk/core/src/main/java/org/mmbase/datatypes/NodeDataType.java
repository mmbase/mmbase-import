/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.Collection;
import org.mmbase.util.Casting;
import org.mmbase.util.LocalizedString;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * The Node data type describes a data type which is based on an MMBase 'node' field. So the value
 * is an MMBase node, which can normally be described by a foreign key.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class NodeDataType extends BasicDataType<Node> {

    private static final Logger log = Logging.getLoggerInstance(NodeDataType.class);

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    protected MustExistRestriction mustExistRestriction = new MustExistRestriction();

    /**
     * Constructor for node field.
     */
    public NodeDataType(String name) {
        super(name, Node.class);
    }


    @Override
    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof NodeDataType) {
            mustExistRestriction.inherit(((NodeDataType)origin).mustExistRestriction);
        }
    }
    @Override
    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof NodeDataType) {
            mustExistRestriction = new MustExistRestriction(((NodeDataType)origin).mustExistRestriction);
        }
    }
    @Override
    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        if (value == null || "".equals(value)) return null;
        Object preCast = preCast(value, node, field); // resolves enumerations
        if (preCast instanceof Node) {
            return preCast;
        }  else {
            Object res = Casting.toType(Node.class, getCloud(node, field), preCast);
            if (res == null) {
                if (Casting.toString(value).equals("-1")) {
                    return null;
                }
                throw new CastException("No such node " + preCast);
            } else {
                return res;
            }
        }
    }

    @Override
    public Integer castForSearch(final Object value, final Node node, final Field field) {
        if (value == null) return -1;
        return org.mmbase.util.Casting.toInteger(value);
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

    @Override
    public int getEnforceStrength() {
        return Math.max(super.getEnforceStrength(), mustExistRestriction.getEnforceStrength());
    }

    @Override
    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value, Node node, Field field) {
        errors = super.validateCastValue(errors, castValue, value, node, field);
        errors = mustExistRestriction.validate(errors, value, node, field);
        return errors;
    }

    protected class MustExistRestriction extends AbstractRestriction<Boolean> {
        private static final long serialVersionUID = 7475598158815232356L;

        MustExistRestriction(MustExistRestriction me) {
            super(me);
            enforceStrength = DataType.ENFORCE_ONCHANGE;
        }
        MustExistRestriction() {
            super("mustExist", Boolean.TRUE);
            enforceStrength = DataType.ENFORCE_ONCHANGE;
        }
        protected Cloud getCloud(Node node, Field field) {
            Cloud cloud = node != null ? node.getCloud() : null;
            if (cloud == null) cloud = field != null ? field.getNodeManager().getCloud() : null;
            if (cloud == null) {
                cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
            }
            return cloud;
        }

        protected boolean simpleValid(Object v, Node node, Field field) {
            if (getValue().equals(Boolean.TRUE)) {
                if (v != null) {
                    if (v instanceof CharSequence) {
                        Cloud cloud = getCloud(node, field);
                        boolean res =  cloud != null && cloud.hasNode(v.toString());
                        if (! res) {
                            log.warn("The cloud " + cloud + " does not have a node " + v);
                        }
                        return res;
                    } else if (v instanceof Number) {
                        int num = ((Number)v).intValue();
                        if (num < 0) return false;
                        Cloud cloud = getCloud(node, field);
                        boolean res =  cloud != null && cloud.hasNode(num);
                        if (! res) {
                            log.warn("The cloud " + cloud + " does not have a node " + v);
                        }
                        return res;
                    } else if (v instanceof Node) {
                        return true;
                    } else {
                        log.debug("Not valid because node value is a " + v.getClass());
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
