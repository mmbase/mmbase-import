/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Enumeration;

/**
 * The collection of clouds, and modules within a Java Virtual Machine.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface CloudContextInterface {

	/**
	 * Retrieves all the modules available in this context
	 * @return all available modules
	 */
	public Iterator getModules();

	
	/**
	 * Retrieves a Module
	 * @param modulename name of the module
	 * @return the requested module
	 */
	public ModuleInterface getModule(String modulename);

	/**
	 * Retrieves all clouds within this context
	 * @return all Clouds within this context
	 */
	public Iterator getClouds();

	/**
	 * Retrieves a Cloud
	 * @param cloudname name of the Cloud
	 * @return all Clouds
	 */
	public CloudInterface getCloud(String cloudname);

	/** 
	 * Retrieve a remote CloudContext
	 * @param cloudcontextUrl place of the remote CloudContext
	 * @return remote CloudContext
	 * public CloudContext getCloudContext(String cloudcontextUrl);
	 */
}
