/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerOrNodeProvider;

import org.mmbase.bridge.*;
/**
 * Interface designed to make it possible for child tags
 * to access a node defined in a tag
 *
 * @author Michiel Meeuwissen 
 * @version $Id: NodeProvider.java,v 1.8 2004-09-15 07:53:46 michiel Exp $ 
 * @todo EXPERIMENTAL
 */

public interface NodeProvider extends TagIdentifier, FunctionContainerOrNodeProvider {
    /**
     * @return the node contained in the tag
     * NOTE: we have decided to call this methid getNodeVar because
     * we use tag attributes with name "node" and type String 
     *
     * Experimental: getGeneratingQuery()
     **/
    Node getNodeVar() throws JspTagException;	
    void setModified();
    void setJspvar(String jv);


    /**
     * Returns a query which (a.o) results this Node.
     * @todo  EXPERIMENTAL
     * @since MMBase-1.8
     */
    Query getGeneratingQuery() throws JspTagException; 


}
