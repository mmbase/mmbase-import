/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.w3c.dom.*;

import org.mmbase.util.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeConfigurer.java,v 1.1 2005-07-29 14:52:37 pierre Exp $
 **/
public class DataTypeConfigurer {

    public static DataTypeConfigurer defaultConfigurer = new DataTypeConfigurer();

    public DataType getDataType(String name) {
        DataType dataType = DataTypes.getDataTypeInstance(name, null);
        if (dataType == null) {
            throw new NotFoundException("Datatype with name " + name + " does not exist.");
        }
        return dataType;
    }

    public TypeSetDefinition getTypeSetDefinition() {
        return new TypeSetDefinition(this);
    }

    public DataTypeDefinition getDataTypeDefinition(TypeSetDefinition typeSetDefinition) {
        return new DataTypeDefinition(typeSetDefinition, this);
    }

    public static DataTypeConfigurer getDefaultConfigurer() {
        return defaultConfigurer;
    }

}
