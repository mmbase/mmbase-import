/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.tagext.*;

/**
 * The TEI class for SetFieldTag to export the fieldname.
 *
 * @author Jaco de Groot
 * @version $Id: SetFieldTEI.java,v 1.5 2005-01-30 16:46:39 nico Exp $ 
 */
public class SetFieldTEI extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo = new VariableInfo[1];
        variableInfo[0] = new VariableInfo("fieldname", "java.lang.String",
                                           true, VariableInfo.NESTED);
        return variableInfo;
    }
}
