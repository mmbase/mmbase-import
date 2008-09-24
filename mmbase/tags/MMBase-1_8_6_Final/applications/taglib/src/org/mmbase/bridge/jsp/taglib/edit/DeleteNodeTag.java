/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.NodeTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

/**
 *
 * As NodeTag, but the node will be removed after the body.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DeleteNodeTag.java,v 1.7 2005-05-04 11:03:12 michiel Exp $ 
 */

public class DeleteNodeTag extends NodeTag {
    
    private Attribute deleteRelations = Attribute.NULL;

    public void setDeleterelations(String r) throws JspTagException {
        deleteRelations = getAttribute(r);
    }
    protected boolean getDeleterelations() throws JspTagException {
        return deleteRelations.getBoolean(this, false);
    }
        
    public int doEndTag() throws JspTagException {    
        org.mmbase.bridge.Node node = getNodeVar();
        if (node != null) {
            node.delete(getDeleterelations()); 
        }
        return super.doEndTag();
    }
}
