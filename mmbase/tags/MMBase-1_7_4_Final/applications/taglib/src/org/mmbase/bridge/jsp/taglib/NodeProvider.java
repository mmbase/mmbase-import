/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerOrNodeProvider;

import org.mmbase.bridge.Node;
/**
 * Interface designed to make it possible for child tags
 * to access a node defined in a tag
 *
 * @author Michiel Meeuwissen 
 * @version $Id: NodeProvider.java,v 1.6 2003-12-18 11:54:37 michiel Exp $ 
 */

public interface NodeProvider extends TagIdentifier, FunctionContainerOrNodeProvider {
    /**
     * @return the node contained in the tag
     * NOTE: we have decided to call this methid getNodeVar because
     * we use tag attributes with name "node" and type String 
     **/
    public Node getNodeVar() throws JspTagException;	
    public void setModified();
    public void setJspvar(String jv);
}
