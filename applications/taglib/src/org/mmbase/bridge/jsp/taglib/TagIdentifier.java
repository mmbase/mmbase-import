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
 * @version $Id: TagIdentifier.java,v 1.3 2003-06-06 10:03:10 pierre Exp $ 
 */

public interface TagIdentifier {
	/**
	* @return the String id the the tag. Id is is a user defined
	* identifier
	**/
	public String getId();	
}
