/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.tagext.*;
import org.mmbase.util.logging.*;

/**
 * The TEI class for SetFieldTag to export the fieldname.
 *
 * @author Jaco de Groot
 * @version $Id: SetFieldTEI.java,v 1.3 2003-06-06 10:03:21 pierre Exp $ 
 */

public class SetFieldTEI extends TagExtraInfo {
    private static Logger log = Logging.getLoggerInstance(SetFieldTEI.class.getName());

    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo = new VariableInfo[1];
        variableInfo[0] = new VariableInfo("fieldname", "java.lang.String",
                                           true, VariableInfo.NESTED);
        return variableInfo;
    }
}
