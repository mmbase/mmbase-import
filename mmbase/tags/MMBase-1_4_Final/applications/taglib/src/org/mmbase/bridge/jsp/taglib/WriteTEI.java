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
* The Export tag exports one jsp variable. Some other tags, such as
* CloudTag and NodeProviders can also export jsp variables by
* themselves.
*
* @author Michiel Meeuwissen
*/
public class WriteTEI extends TagExtraInfo {
    
    public WriteTEI() { 
        super(); 
    }

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
            
            String typeAttribute    = (String) data.getAttribute("type"); 
            if (typeAttribute == null) typeAttribute = defaultType();           
            String type;
            
            if ("Object".equalsIgnoreCase(typeAttribute)) {
                type = "java.lang.Object";
            } else if ("String".equalsIgnoreCase(typeAttribute)) {
                type = "java.lang.String";
            } else if ("Node".equalsIgnoreCase(typeAttribute)) {
                type = "org.mmbase.bridge.Node";
            } else if ("Integer".equalsIgnoreCase(typeAttribute)) {
                type = "java.lang.Integer";
            } else if ("Vector".equalsIgnoreCase(typeAttribute)) {
                type = "java.util.Vector";
            } else {
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
