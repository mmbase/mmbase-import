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
 * @version $Id: NodeDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class NodeDataType extends DataType {

    public static final String PROPERTY_MUSTEXIST = "mustExist";
    public static final Boolean PROPERTY_MUSTEXIST_DEFAULT = Boolean.TRUE;

    protected DataType.Property mustExistProperty = null;

    /**
     * Constructor for node field.
     */
    public NodeDataType(String name) {
        super(name, MMObjectNode.class);
        mustExistProperty = createProperty(PROPERTY_MUSTEXIST, PROPERTY_MUSTEXIST_DEFAULT);
        mustExistProperty.setFixed(true);
    }

    public DataType.Property getMustExistProperty() {
        return mustExistProperty;
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
        if (value !=null) {
            MMObjectNode nodeValue = Casting.toNode(value,MMBase.getMMBase().getTypeDef());
            if (nodeValue == null) {
                failOnValidate(getMustExistProperty(), value, cloud);
            }
        }
    }

    public Object clone(String name) {
        NodeDataType clone = (NodeDataType)super.clone(name);
        clone.mustExistProperty = (DataType.Property)getMustExistProperty().clone(clone);
        return clone;
    }

}
