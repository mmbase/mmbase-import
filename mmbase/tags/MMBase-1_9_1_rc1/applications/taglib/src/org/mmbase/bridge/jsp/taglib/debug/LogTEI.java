/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.debug;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;


/**
 * A TEI for a Logger instance.
 *
 * @author Michiel Meeuwissen 
 * @version $Id: LogTEI.java,v 1.2 2003-06-06 10:03:16 pierre Exp $ 
 */

public class LogTEI extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo[] variableInfo = null;

        String jspvarAttribute  = (String) data.getAttribute("jspvar"); 

        if (jspvarAttribute != null) {
            variableInfo = new VariableInfo[1];                                   
            variableInfo[0] =  new VariableInfo(jspvarAttribute,
                                                "org.mmbase.util.logging.Logger",
                                                true,
                                                VariableInfo.NESTED);
        }
        return variableInfo;
    }        
}
