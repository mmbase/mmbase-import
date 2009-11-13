/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

/**
 * Interface designed to make it possible for child tags
 * to access a parent tag using a String id
 *
 * @author Kees Jongenburger
 * @version $Id$ 
 */

public interface TagIdentifier {
    /**
     * @return the String id the the tag. Id is is a user defined
     * identifier
     */
    public String getId();	
}
