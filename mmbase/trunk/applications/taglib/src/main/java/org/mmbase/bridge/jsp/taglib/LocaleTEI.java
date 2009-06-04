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
 * TEI class for the Locale tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
s */

public class LocaleTEI extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo[] variableInfo = null;
        String jspvarAttribute  = (String) data.getAttribute("jspvar");
        if (jspvarAttribute != null) {
            variableInfo = new VariableInfo[1];
            variableInfo[0] =  new VariableInfo(jspvarAttribute, java.util.Locale.class.getName(), true, VariableInfo.NESTED);
        }
        return variableInfo;
    }

}
