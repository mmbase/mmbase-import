/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.List;

/**
 * A list of node managers
 *
 * @author Pierre van Rooden
 */
public interface NodeManagerList extends List {

	/**
	*
	*/
	public NodeManager getNodeManager(int index);

	/**
	*
	*/
	public NodeManagerIterator nodeManagerIterator();

}
