/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * Modules are pieces of functionality that are not MMBase objects.
 * e.g. Session, Mail, Upload and other functionality
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface Module {

 	/**
     * Retrieves the CloudContext to which this module belongs
     */
    public CloudContext getCloudContext();

 	/**
	 * Retrieve the name of the module.
	 * @param language the language in which you want the name
	 */
	public String getName(String language);

	/**
     * Retrieve the name of the module (in the default language defined in mmbaseroot.xml).
     */
    public String getName();

	/**
	 * Retrieve the description of the module.
	 * @param language the language in which you want the description
	 */
	public String getDescription(String language);

	/** 
	 * Retireve the description of the module.
	 */
	public String getDescription();

}
