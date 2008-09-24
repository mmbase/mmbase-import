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
 * Interface designed to make it possible for child tags to access a node defined in a tag
 *
 * @author Michiel Meeuwissen 
 * @version $Id: NodeProvider.java,v 1.12 2006-05-17 19:14:35 michiel Exp $ 
 */

public interface NodeProvider extends TagIdentifier, FunctionContainerOrNodeProvider {

    /**
     * @return the node contained in the tag
     * NOTE: we have decided to call this method getNodeVar because
     * we use tag attributes with name "node" and type String 
     **/
    Node getNodeVar() throws JspTagException;	

    /**
     * NodeProviders support the jspvar attribute (giving a Node jsp var object).
     */
    void setJspvar(String jv);


    /**
     * Returns a query which (a.o) results this Node.
     * @todo  EXPERIMENTAL
     * @since MMBase-1.8
     */
    Query getGeneratingQuery() throws JspTagException; 


    /**
     * Whether the node must be commited after the body of the tag if any changes occured
     * @since MMBase-1.8
     */
    void setCommitonclose(String c) throws JspTagException;

}
