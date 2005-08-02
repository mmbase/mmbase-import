/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import java.util.Map;
import java.util.HashMap;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.w3c.dom.*;

import org.mmbase.util.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: DataTypeConfigurer.java,v 1.3 2005-08-02 14:29:26 pierre Exp $
 * @since MMBase-1.8
 **/
public class DataTypeConfigurer {

    protected DataTypeCollector collector = null;

    public DataTypeConfigurer(DataTypeCollector collector) {
        this.collector = collector;
    }

    public DataType getDataType(String name) {
        return collector.getDataTypeInstance(name, null);
    }

    public void addDataType(DataType dataType) {
        collector.addDataType(dataType);
    }

    public void rewrite(DataType dataType) {
        if (collector != null) {
            collector.rewrite(dataType);
        } else {
            throw new IllegalStateException("Cannot rewrite datatype, no collector present to finish it");
        }
    }

    public void finish(DataType dataType) {
        if (collector != null) {
            collector.finish(dataType);
        } else {
            dataType.finish(null);
        }
    }

    public DataTypeDefinition getDataTypeDefinition() {
        return new DataTypeDefinition(this);
    }

}
