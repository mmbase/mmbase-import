/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;

/**
 * Modules are pieces of functionality that are not MMBase objects.
 * e.g. Session, Mail, Upload and other functionality
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicModule implements org.mmbase.bridge.Module {

    // link to cloud context
    private BasicCloudContext cloudContext = null;

    private org.mmbase.module.Module module = null;

    /**
     *  constructor to call from the CloudContext class
     *  (package only, so cannot be reached from a script)
     */
    BasicModule(org.mmbase.module.Module mod, CloudContext cloudcontext) {
        cloudContext=(BasicCloudContext)cloudcontext;
        module = mod;
    }
 	
 	/**
     * Retrieves the Cloud to which this module belongs
     */
    public CloudContext getCloudContext() {
        return cloudContext;
    }

 	/**
	 * Retrieve the name of the module.
	 * @param language the language in which you want the name
	 */
	public String getName(String language) {
	    // not yet implemented
        return module.getName();
    }

	/**
     * Retrieve the name of the module (in the default language defined in mmbaseroot.xml)
     */
    public String getName() {
        return module.getName();
    }

	/**
	 * Retrieve the description of the module.
	 * @param language the language in which you want the description
	 */
	public String getDescription(String language) {
	    // not yet implemented
        return module.getModuleInfo();
    }

	/** 
	 * Retrieve the description of the module.
	 */
	public String getDescription() {
        return module.getModuleInfo();
    }

}
