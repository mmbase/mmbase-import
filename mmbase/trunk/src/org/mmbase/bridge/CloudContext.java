/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.List;

/**
 * The collection of clouds, and modules within a Java Virtual Machine.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface CloudContext {

	/**
	 * Retrieves all the modules available in this context
	 * @return all available modules
	 */
	public ModuleList getModules();
	
	/**
	 * Retrieves a Module
	 * @param modulename name of the module
	 * @return the requested module
	 */
	public Module getModule(String modulename);

	/**
	 * Retrieves the names of all clouds within this context
	 * @return a <code>List</code> of all Cloudnames within this contextas a <code>String</code>
	 */
	public CloudList getClouds();

	/**
	 * Retrieves a Cloud.
	 * @param cloudname name of the Cloud
	 * @return all Clouds
	 */
	public Cloud getCloud(String cloudname);

 }
