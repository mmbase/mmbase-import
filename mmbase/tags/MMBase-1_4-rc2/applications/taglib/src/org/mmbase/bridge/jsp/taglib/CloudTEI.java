/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

/**
* The TEI class belonging to the CloudTag and descendents
*
* @author Michiel Meeuwissen
**/
public class CloudTEI extends TagExtraInfo {
    
    public CloudTEI() { 
        super(); 
    }

    protected String defaultCloudVar() {
        return CloudTag.DEFAULT_CLOUD_JSPVAR;
    }
    protected String cloudType() {
        return "org.mmbase.bridge.Cloud";
    }
        
    /**
    * Implementation of TagExtraInfo return values declared here
    * should be filled at one point, in this case with the CloudTag.
    **/
    public VariableInfo[] getVariableInfo(TagData data){      
        VariableInfo[] variableInfo = null;
        variableInfo    =  new VariableInfo[1];
        Object idObject = data.getAttribute("jspvar");
        String cloudVarName = defaultCloudVar();
        if (idObject != null){
            if (idObject == TagData.REQUEST_TIME_VALUE) { // then of course we cannot set a variable with that name 
            } else {
                cloudVarName = "" + idObject;
            }
        }

        variableInfo[0] =  new VariableInfo(cloudVarName, cloudType(), true, VariableInfo.NESTED);
        return variableInfo;
    }
        
}
