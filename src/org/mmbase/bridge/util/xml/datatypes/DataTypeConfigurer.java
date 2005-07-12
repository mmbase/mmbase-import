/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.datatypes;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.DataTypes;
import org.w3c.dom.*;

import org.mmbase.util.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeConfigurer.java,v 1.2 2005-07-12 15:03:36 pierre Exp $
 **/
public class DataTypeConfigurer {

    /** XSD resource filename of the datatypes XSD version 1.0 */
    public static final String XSD_DATATYPES_1_0 = "datatypes.xsd";
    /** XSD namespace of the datatypes XSD version 1.0 */
    public static final String NAMESPACE_DATATYPES_1_0 = "http://www.mmbase.org/xmlns/datatypes";
    /** XSD resource filename of the enumeration query (expansion of standard searchquery) XSD version 1.0 */
    public static final String XSD_ENUMERATIONQUERY_1_0 = "datatypes.xsd";
    /** XSD namespace of the enumeration query XSD version 1.0 */
    public static final String NAMESPACE_ENUMERATIONQUERY_1_0 = "http://www.mmbase.org/xmlns/datatypes";

    /** XSD namespace of the datatypes XSD, most recent version */
    public static final String NAMESPACE_DATATYPES = NAMESPACE_DATATYPES_1_0;

    /** XSD namespace of the enumeration query XSD, most recent version */
    public static final String NAMESPACE_ENUMERATIONQUERY = NAMESPACE_ENUMERATIONQUERY_1_0;    public static DataTypeConfigurer defaultConfigurer = new DataTypeConfigurer();

    /**
     * Register the namespace and XSD used by DataTypeConfigurer
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(NAMESPACE_DATATYPES_1_0, XSD_DATATYPES_1_0, DataTypeConfigurer.class);
        XMLEntityResolver.registerPublicID(NAMESPACE_ENUMERATIONQUERY_1_0, XSD_ENUMERATIONQUERY_1_0, DataTypeConfigurer.class);
    }

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
