/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
* As NodeTag, but the node will be removed after the body.
*
* @author Michiel Meeuwissen
*/
public class DeleteNodeTag extends NodeTag {
    
    private static Logger log = Logging.getLoggerInstance(DeleteNodeTag.class.getName());
        
    public int doAfterBody() throws JspTagException {    
        getNodeVar().remove();
        return super.doAfterBody();
    }
}
