/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.util.logging.*;


/**
 * The TEI class for Field (type) Providers. in fact a simplified version of WriterTEI (can only provide Field an FieldValue)
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldProviderTEI.java,v 1.2 2003-10-13 08:34:23 keesj Exp $ 
 * @since MMBase-1.7
 */

public class FieldProviderTEI extends WriterTEI {
    
    private static final Logger log = Logging.getLoggerInstance(FieldProviderTEI.class);    

    protected String defaultType() {
        return "field";
    }

    protected String getType(String  typeAttribute) {
        String type;
        switch (WriterHelper.stringToType(typeAttribute)) {
            case WriterHelper.TYPE_FIELD:
                type = org.mmbase.bridge.Field.class.getName(); break;
            case WriterHelper.TYPE_FIELDVALUE:
                type = org.mmbase.bridge.FieldValue.class.getName(); break;
            default:
                //type = "java.lang.Object"; 
                throw new RuntimeException("Unsupported type '" + typeAttribute + "'");
            }
        return type;
     
    }


    public FieldProviderTEI() { 
        super(); 
    }

    
}
