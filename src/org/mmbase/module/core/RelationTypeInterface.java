/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Enumeration;

/**
 *
 * @author Rob Vermeulen
 */
public interface RelationTypeInterface extends NodeTypeInterface {

	/**
	 * gets the role of the source to the destination
	 * @return the role
	 */
	public String getForwardRole();

	/**
	 * gets the role of the destination to the source
	 * @return the role
	 */
	public String getReciprocalRole();
}
