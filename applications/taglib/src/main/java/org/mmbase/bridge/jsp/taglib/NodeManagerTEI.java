/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.NodeManager;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

/**
 * The TEI class for NodeProviders. A NodeProvider can export one or more
 * 'Nodes', and it also can export fields (if the `fields' attribute is
 * given).
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeProviderTEI.java 35335 2009-05-21 08:14:41Z michiel $
 */

public class NodeManagerTEI extends TagExtraInfo {

    public NodeManagerTEI() {
        super();
    }

    public VariableInfo[] getVariableInfo(TagData data){
        String id = "";
        Object idObject = data.getAttribute("jspvar");
        if (idObject != null){
            id = "" + idObject;
        }
        VariableInfo[] variableInfo = new VariableInfo[1];
        variableInfo[0] = new VariableInfo(id,
                                           NodeManager.class.getName(),
                                           true,
                                           VariableInfo.NESTED);
        return variableInfo;
    }

}
