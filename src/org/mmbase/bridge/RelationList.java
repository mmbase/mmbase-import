/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Collection;

/**
 * A list of Relations
 *
 * @author Pierre van Rooden
 */
public interface RelationList {

	/**
	*
	*/
	public Relation get(int index);

	/**
	*
	*/
	public boolean isEmpty();
	
	/**
	*
	*/
//	public Relation Iterator iterator();

	/**
	*
	*/
    public int size();
		
}
