/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import java.util.List;

import org.mmbase.bridge.jsp.taglib.*;

/**
 * Function Container can be used around Function (-like) Tags
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: FunctionContainer.java,v 1.4 2003-08-29 12:12:25 keesj Exp $
 */
public interface FunctionContainer extends ParamHandler, TagIdentifier {


    /**
     * Gets the parameters (provided by param sub-tags).
     * @see ParamHandler#addParameter
     * @return List of parameters (in order of setting if there was no definition)
     * @return Arguments (if there was a definition set)
     */
    List    getParameters();
}
