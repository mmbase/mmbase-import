/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.NodeTag;

/**
 *
 * As NodeTag, but the node will be removed after the body.
 *
 * @author Michiel Meeuwissen
 */
public class DeleteNodeTag extends NodeTag {
    
    private boolean deleteRelations = false;

    public void setDeleterelations(boolean r) {
        deleteRelations = r;
    }
        
    public int doAfterBody() throws JspTagException {    
        getNodeVar().delete(deleteRelations);       
        return super.doAfterBody();
    }
}
