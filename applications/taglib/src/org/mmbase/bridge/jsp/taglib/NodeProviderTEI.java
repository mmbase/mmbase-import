/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

import java.util.Vector; 
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
* The TEI class for NodeProviders.
*
* @author Michiel Meeuwissen
**/
public class NodeProviderTEI extends TagExtraInfo {
    
    private static Logger log = Logging.getLoggerInstance(NodeProviderTEI.class.getName());    

    public NodeProviderTEI() { 
        super(); 
    }

    /**
    * implementation of TagExtraInfo return values declared here
    * should be filled at one point, currently function fillVars is responsible for
    * that and gets called before every iteration
    **/
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo =    null;
        //this method is called /before/ the values are set
        //so we can not use the data members in this class
        //but the TagData provides the necessary data
        //in effect we have to parse the data twice
        //once here and onces specific attributes are set
        //maybe this can be done better I do not know
        
        
        //The tag parameter fields defines what variables should be available
        //within the body of the tag. 
        //If an 'id' is defined as well (eg 'mynode') then this will be prefixed to the
        //fieldnames: mynode_title.
        
        Object fieldsAttribute  = data.getAttribute("fields"); 
        Vector fields;
        
        if (TagData.REQUEST_TIME_VALUE == fieldsAttribute) {
            log.debug("Cannot set field variables for request time value");
            fields = new Vector();
        } else {
            String fieldsString = (String) fieldsAttribute;
            if (fieldsString == null) {
                fields = new Vector();
            } else {
                fields  = AbstractNodeProviderTag.stringSplitter(fieldsString, ",");
            }
        }
        
        int nodeVariable = 0; 
        // Number of node-variables to be defined. 
        // is 0 or 1 now, but will become more for multi-level lists (not yet ready)
        
        
        String id = "";
        Object idObject = data.getAttribute("id");
        if (idObject != null){
            if (idObject == TagData.REQUEST_TIME_VALUE) { // then of course we cannot set a variable with that name                             
            } else {
                id = "" + data.getAttribute("id");
                nodeVariable = 1;
            }
        }
        
        variableInfo =    new VariableInfo[(fields.size()) + nodeVariable];
        int j = 0;
        for (int i = 0 ; i < fields.size(); i++){
            String field = (String)fields.elementAt(i);
            //it would be nice to return Integer is a field is of that type
            
            // michiel: I think we should deprecate also this.
            
            // log.debug("will set " + getSimpleReturnValueName(id, field));
            variableInfo[j++] = new VariableInfo(AbstractNodeProviderTag.getSimpleReturnValueName(id, field),
                                                 "java.lang.String",
                                                 true,
                                                 VariableInfo.NESTED);
            /* michiel: this does not make much sense. why would someone want to use 'item1', if you have 
               a name to use. It also clashes with a builder which has a fieldname 'item1'.
               variableInfo[j++] = new VariableInfo(prefix + "item" + (i+1),
               "java.lang.String",
               true,
               VariableInfo.NESTED);            
            */
        }
        if (nodeVariable == 1) {
            variableInfo[j] = new VariableInfo(id,
                "org.mmbase.bridge.Node",
                true,
                VariableInfo.NESTED);
        }
        return variableInfo;
    }
        
}
