/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.*;

import java.util.List;
import org.mmbase.util.Argument;
import javax.servlet.jsp.JspTagException;

/**
 * Function Container can be used around Function (-like) Tags
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: FunctionContainer.java,v 1.1 2003-07-23 17:46:57 michiel Exp $
 */
public interface FunctionContainer extends ParamHandler, TagIdentifier {

    /**
     * Defines the possible arguments of the function.
     * 
     * @throws JspTagException If the definition was set already, or if already (wrong) parameters were set.
     */
    void setDefinition(Argument[] args) throws JspTagException;

    /**
     * Gets the function result.
     * @throws JspTagException if the function result is not yet available
     */
    Object getResult() throws JspTagException;

    /**
     * Sets the function result (to be used by sub-tags).
     * @throws JspTagException if the function result was set already
     */
    void   setResult(Object result) throws JspTagException;

    /**
     * Gets the parameters (provided by param sub-tags).
     * @see ParamHandler#addParameter
     * @return List of parameters (in order of setting if there was no definition)
     * @return Arguments (if there was a definition set)
     */
    List    getParameters();
}
