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
* The TEI class belonging to the CloudTag.
*
* @author Michiel Meeuwissen
**/
public class CloudTEI extends TagExtraInfo {
    

    public CloudTEI() { 
        super(); 
    }
        
    /**
    * Implementation of TagExtraInfo return values declared here
    * should be filled at one point, in this case with the CloudTag.
    **/
    public VariableInfo[] getVariableInfo(TagData data){      
        VariableInfo[] variableInfo = null;
        variableInfo    =  new VariableInfo[1];
        variableInfo[0] =  new VariableInfo("cloud", "org.mmbase.bridge.Cloud", true, VariableInfo.NESTED);
        return variableInfo;
    }
        
}
