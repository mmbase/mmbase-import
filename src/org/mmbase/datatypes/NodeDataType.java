/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: NodeDataType.java,v 1.6 2005-08-18 12:21:51 pierre Exp $
 * @since MMBase-1.8
 */
public class NodeDataType extends DataType {

    public static final String PROPERTY_MUSTEXIST = "mustExist";
    public static final Boolean PROPERTY_MUSTEXIST_DEFAULT = Boolean.TRUE;

    protected DataType.Property mustExistProperty;

    /**
     * Constructor for node field.
     */
    public NodeDataType(String name) {
        super(name, MMObjectNode.class);
    }

    public void erase() {
        super.erase();
        mustExistProperty = null;
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof NodeDataType) {
            NodeDataType dataType = (NodeDataType)origin;
            mustExistProperty = inheritProperty(dataType.mustExistProperty);
        }
    }

    public DataType.Property getMustExistProperty() {
        if (mustExistProperty == null) {
            mustExistProperty = createProperty(PROPERTY_MUSTEXIST, PROPERTY_MUSTEXIST_DEFAULT);
            mustExistProperty.setFixed(true);
        }
        return mustExistProperty;
    }

    public void validate(Object value, Node node, Field field, Cloud cloud) {
        super.validate(value, node, field, cloud);
        if (value != null && !(value instanceof Number && ((Number)value).intValue() == -1)) {
            MMObjectNode nodeValue = Casting.toNode(value,MMBase.getMMBase().getTypeDef());
            if (nodeValue == null) {
                failOnValidate(getMustExistProperty(), value, cloud);
            }
        }
    }

}
