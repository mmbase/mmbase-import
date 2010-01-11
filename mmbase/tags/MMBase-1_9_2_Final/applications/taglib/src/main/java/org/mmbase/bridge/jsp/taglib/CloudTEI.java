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
 * @version $Id$
 */

public class CloudTEI extends TagExtraInfo {

    protected String cloudType() {
        return org.mmbase.bridge.Cloud.class.getName();
    }

    /**
     * Implementation of TagExtraInfo return values declared here
     * should be filled at one point, in this case with the CloudTag.
     **/
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo = null;

        String jspvar  = (String) data.getAttribute("jspvar");

        if (jspvar != null) {
            variableInfo    =  new VariableInfo[1];
            variableInfo[0] =  new VariableInfo(jspvar, cloudType(), true, VariableInfo.NESTED);
        }
        return variableInfo;
    }

}
