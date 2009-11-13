/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.community.taglib;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

/**
 * The GetInfo tag can export one jsp variable.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
 
public class PostTEI extends TagExtraInfo {

    public PostTEI() {
        super();
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        String jspvarAttribute  = (String) data.getAttribute("jspvar");
        if (jspvarAttribute==null) {
            return  new VariableInfo[0];
        } else {
            VariableInfo[] variableInfo =  new VariableInfo[1];
            variableInfo[0] =  new VariableInfo(jspvarAttribute,
                                            "java.lang.String",
                                            true,
                                            VariableInfo.AT_END);
            return variableInfo;
        }
    }
}
