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
