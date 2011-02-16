/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

/**
 * The TEI class belonging to the QueryContainer tags.
 *
 * @author Michiel Meeuwissen
 * @version $Id: QueryTEI.java 35335 2009-05-21 08:14:41Z michiel $
 * @since MMBase-1.9.3
 */

public class NodeQueryTEI extends TagExtraInfo {

    /**
     * Implementation of TagExtraInfo return values declared here
     * should be filled at one point, in this case with the CloudTag.
     **/
    @Override
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo = null;

        String jspvar  = (String) data.getAttribute("jspvar");

        if (jspvar != null) {
            variableInfo    =  new VariableInfo[1];
            variableInfo[0] =  new VariableInfo(jspvar, org.mmbase.bridge.NodeQuery.class.getName(), true, VariableInfo.NESTED);
        }
        return variableInfo;
    }

}
