/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.HashMap;
import java.util.List;

/**
 * The collection of clouds and modules within a Java Virtual Machine.
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
	 * @param authenticationtype   	   the type of authentication, which should be 
	 * 	    	    	    	   used by the authentication implementation.
	 * @param loginInfo 	    	    the user related login information.
	 * @return                         the requested cloud
    	 * @throws CloudNotFoundException  if the specified cloud could not be found
	 */
	public Cloud getCloud(String name, String authenticationtype, HashMap loginInfo);
	
	/**
	 * Returns the names of all the clouds known to the system
    	 *
	 * @return                      A StringList of all clouds names known to 
	 *   	    	    	    	our Context
	 */
	public StringList getCloudNames();
 }
