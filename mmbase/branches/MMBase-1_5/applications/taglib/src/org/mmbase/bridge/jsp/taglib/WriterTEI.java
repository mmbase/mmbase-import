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
 **/
public class WriterTEI extends TagExtraInfo {
    
    protected int scope() {
        return VariableInfo.NESTED;
    }
    protected String defaultType() {
        return "Object";
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo[] variableInfo = null;

        String jspvarAttribute  = (String) data.getAttribute("jspvar"); 

        if (jspvarAttribute != null) {
            variableInfo = new VariableInfo[1];
            
            String typeAttribute    = (String) data.getAttribute("vartype"); 
            if (typeAttribute == null) typeAttribute = defaultType();           
            String type;
            switch (WriterHelper.stringToType(typeAttribute)) {
            case WriterHelper.TYPE_OBJECT:
                type = "java.lang.Object"; break;
            case WriterHelper.TYPE_STRING:
                type = "java.lang.String"; break;
            case WriterHelper.TYPE_NODE:
                type = "org.mmbase.bridge.Node"; break;
            case WriterHelper.TYPE_CLOUD:
                type = "org.mmbase.bridge.Cloud"; break;
            case WriterHelper.TYPE_TRANSACTION:
                type = "org.mmbase.bridge.Transaction"; break;
            case WriterHelper.TYPE_DECIMAL:
                type = "java.math.BigDecimal"; break;
            case WriterHelper.TYPE_INTEGER:
                type = "java.lang.Integer"; break;
            case WriterHelper.TYPE_DOUBLE:
                type = "java.lang.Double"; break;
            case WriterHelper.TYPE_FLOAT:
                type = "java.lang.Float"; break;
            case WriterHelper.TYPE_LONG:
                type = "java.lang.Long"; break;
            case WriterHelper.TYPE_VECTOR:// deprecated
                type = "java.util.Vector"; break;
            case WriterHelper.TYPE_LIST:
                type = "java.util.List"; break;
            case WriterHelper.TYPE_BYTES:
                type = "[B"; break; 
                // this doesn't work like this...
                // How it does??

                //Class.forName("[B").getName(); break;
            default:
                //type = "java.lang.Object"; 
                throw new RuntimeException("Unknown type '" + typeAttribute + "'");
            }
                        
            variableInfo[0] =  new VariableInfo(jspvarAttribute,
                                                type,
                                                true,
                                                scope());
        }
        return variableInfo;
    }        
}
