/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;


/**
 * Writer tags can also produce a jsp variable, for use in the
 * body. This is the TEI class which is needed for that.
 *
 * @author Michiel Meeuwissen 
 * @version $Id$ 
 */

public class WriterTEI extends TagExtraInfo {
    
    protected int scope() {
        return VariableInfo.NESTED;
    }
    protected String defaultType() {
        return "object";
    }

    /**
     * @since MMBase-1.7
     */
    protected String getType(String  typeAttribute) {
        String type;
        switch (WriterHelper.stringToType(typeAttribute)) {
        case WriterHelper.TYPE_OBJECT:
            type = Object.class.getName(); break;
        case WriterHelper.TYPE_STRING:
            type = String.class.getName(); break;
        case WriterHelper.TYPE_CHARSEQUENCE:
            type = CharSequence.class.getName(); break;
        case WriterHelper.TYPE_NODE:
            type = org.mmbase.bridge.Node.class.getName(); break;
        case WriterHelper.TYPE_CLOUD:
            type = org.mmbase.bridge.Cloud.class.getName(); break;
        case WriterHelper.TYPE_TRANSACTION:
            type = org.mmbase.bridge.Transaction.class.getName(); break;
        case WriterHelper.TYPE_DECIMAL:
            type = java.math.BigDecimal.class.getName(); break;
        case WriterHelper.TYPE_INTEGER:
            type = Integer.class.getName(); break;
        case WriterHelper.TYPE_DOUBLE:
            type = Double.class.getName(); break;
        case WriterHelper.TYPE_FLOAT:
            type = Float.class.getName(); break;
        case WriterHelper.TYPE_LONG:
            type = Long.class.getName(); break;
        case WriterHelper.TYPE_VECTOR:// deprecated
            type = java.util.Vector.class.getName(); break;
        case WriterHelper.TYPE_SET:
            type = java.util.Set.class.getName(); break;
        case WriterHelper.TYPE_LIST:
            type = java.util.List.class.getName(); break;
        case WriterHelper.TYPE_DATE:
            type = java.util.Date.class.getName(); break;
        case WriterHelper.TYPE_FIELD:
            type = org.mmbase.bridge.Field.class.getName(); break;
        case WriterHelper.TYPE_FIELDVALUE:
            type = org.mmbase.bridge.FieldValue.class.getName(); break;
        case WriterHelper.TYPE_BOOLEAN:
            type = Boolean.class.getName(); break;
        case WriterHelper.TYPE_BYTES:
            type = "byte[]"; break; 
            // this doesn't work like this...
            // How it does??

            //Class.forName("[B").getName(); break;
        case WriterHelper.TYPE_FILEITEM:
            type = org.apache.commons.fileupload.FileItem.class.getName();
            break; 
        default: 
            type = typeAttribute;
            //default:
            //type = "java.lang.Object"; 
            //throw new RuntimeException("Unknown type '" + typeAttribute + "'");
        }
        return type;
   
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo[] variableInfo = null;

        String jspvarAttribute  = (String) data.getAttribute("jspvar"); 

        if (jspvarAttribute != null) {
            variableInfo = new VariableInfo[1];
            
            String typeAttribute    = (String) data.getAttribute("vartype"); 
            if (typeAttribute == null) typeAttribute = defaultType();           
            String type = getType(typeAttribute);
                        
            variableInfo[0] =  new VariableInfo(jspvarAttribute,
                                                type,
                                                true,
                                                scope());
        }
        return variableInfo;
    }        
}
