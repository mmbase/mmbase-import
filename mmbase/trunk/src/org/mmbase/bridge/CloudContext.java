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
 * @author Jaco de Groot
 */
public interface CloudContext {

	/**
	 * Returns all modules available in this context.
    	 *
	 * @return all available modules
	 */
	public ModuleList getModules();
	
	/**
	 * Returns the module with the specified name.
    	 *
	 * @param name                      the name of the module to be returned
	 * @return                          the requested module
    	 * @throws ModuleNotFoundException  if the specified module could not be
    	 *                                  found
	 */
	public Module getModule(String name);

	/**
	 * Returns all clouds available in this context.
    	 *
	 * @return  all available clouds
	 */
	public CloudList getClouds();

	/**
	 * Returns the cloud with the specified name.
    	 *
	 * @param name                     the name of the cloud to be returned
	 * @return                         the requested cloud
    	 * @throws CloudNotFoundException  if the specified cloud could not be found
	 */
	public Cloud getCloud(String name);

	/**
	 * Returns the cloud with the specified name, with authentication
    	 *
	 * @param name                     the name of the cloud to be returned
	 * @param application	    	   the type of authentication, which should be 
	 * 	    	    	    	   used by the authentication implementation.
	 * @param user	    	    	   the user related information.
	 * @return                         the requested cloud
    	 * @throws CloudNotFoundException  if the specified cloud could not be found
	 */
	public Cloud getCloud(String name, String application, User user);

	/**
	 * Returns the cloud with the specified name in readonly mode if requested.
    	 * If a cloud is in readonly mode no transactions are possible, nor any
    	 * edits/changes to the cloud. This can be used for optimization.
    	 *
	 * @param name                     the name of the cloud to be returned
	 * @param readonly                 <code>true</code> if the returned cloud
    	 *                                 should be in readonly mode
	 * @return                         the requested cloud
    	 * @throws CloudNotFoundException  if the specified cloud could not be found
	 */
	public Cloud getCloud(String name, boolean readonly);

 }
