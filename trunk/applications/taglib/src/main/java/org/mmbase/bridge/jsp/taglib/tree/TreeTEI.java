/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

/**
 * The TEI class belonging to the TreeContainer.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class TreeTEI extends TagExtraInfo {

    /**
     **/
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo = null;

        String jspvar  = (String) data.getAttribute("jspvar");

        if (jspvar != null) {
            variableInfo    =  new VariableInfo[2];
            variableInfo[0] =  new VariableInfo(jspvar, org.mmbase.bridge.util.TreeList.class.getName(), true, VariableInfo.NESTED);
            variableInfo[1] =  new VariableInfo("TAGINFO", "Integer", true, VariableInfo.NESTED);
        } else {
            variableInfo    =  new VariableInfo[1];
            variableInfo[0] =  new VariableInfo("TAGINFO", "Integer", true, VariableInfo.NESTED);
        }
        return variableInfo;
    }

}
