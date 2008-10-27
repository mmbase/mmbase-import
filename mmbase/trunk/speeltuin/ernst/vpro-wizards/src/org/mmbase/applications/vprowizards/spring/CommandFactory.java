/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

/**
 * implement this interface to create a factory for creating commands of a specific type.
 * the difference between command implementations is mostly how they know what commands to support.
 * (what kind of configuration)
 * @author ebunders
 *
 */
public interface CommandFactory {
	
	public Command getNewInstance();
	
}
