/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import java.util.List;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.functions.*;
import javax.servlet.jsp.JspTagException;

/**
 * Function Container can be used around Function (-like) Tags
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: FunctionContainer.java,v 1.5 2003-12-18 09:05:41 michiel Exp $
 */
public interface FunctionContainer extends ParamHandler, TagIdentifier, FunctionContainerOrNodeProvider {


    /**
     * Gets the parameters (provided by param sub-tags, referid).
     * @see ParamHandler#addParameter
     * @return List of parameters (in order of setting if there was no definition)
     */
    List getParameters();


    /**
     * Returns the function-name.
     * @return The name of the function to be evaluated (or null, it this remains unspecified).
     */
    String  getName() throws JspTagException;
    

    interface Entry {
        String getKey();
        Object getValue();
    }
}
