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
public interface CloudContextInterface {

	/**
	 * gives the value of an InitParameter
	 * @param parameter the initparamter
	 * @return the value of the initparameter
	 */
	public String getInitParameter(String parameter);


	/**
	 * gives all the modules
	 * @return all module
	 */
	public Enumeration getModules();

	
	/**
	 * gives a Module
	 * @param name of the module
	 * @return the requested module
	 */
	public CloudInterface getModules(String modulename);

	/**
	 * gives all clouds 
	 * @return all Clouds
	 */
	public Enumeration getClouds();

	/**
	 * gives a Cloud
	 * @param name of the Cloud
	 * @return all Clouds
	 */
	public CloudInterface getCloud(String cloudname);

	/** 
	 * give remote CloudContext
	 * @param place of remote CloudContext
	 * @return remote CloudContext
	 *
	 * public CloudContext getCloudContext(String cloudcontextUrl);
	 */
}
