/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Collection;

/**
 * A list of Relation Managers
 *
 * @author Pierre van Rooden
 */
public interface RelationManagerList {

	/**
	*
	*/
	public RelationManager get(int index);

	/**
	*
	*/
	public boolean isEmpty();
	
	/**
	*
	*/
//	public RelationManagerIterator iterator();

	/**
	*
	*/
    public int size();
		
}
