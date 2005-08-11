/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: NodeDataType.java,v 1.4 2005-08-11 14:41:40 pierre Exp $
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
        mustExistProperty = createProperty(PROPERTY_MUSTEXIST, PROPERTY_MUSTEXIST_DEFAULT);
        mustExistProperty.setFixed(true);
    }

    public void inherit(DataType origin) {
        super.inherit(origin);
        if (origin instanceof NodeDataType) {
            NodeDataType dataType = (NodeDataType)origin;
            mustExistProperty = (DataType.Property)dataType.getMustExistProperty().clone(this);
        }
    }

    public DataType.Property getMustExistProperty() {
        return mustExistProperty;
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value, cloud);
        if (value != null && !(value instanceof Number && ((Number)value).intValue() == -1)) {
            MMObjectNode nodeValue = Casting.toNode(value,MMBase.getMMBase().getTypeDef());
            if (nodeValue == null) {
                failOnValidate(getMustExistProperty(), value, cloud);
            }
        }
    }

}
