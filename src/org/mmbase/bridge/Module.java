/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Hashtable;
import javax.servlet.*;

/**
 * Modules are pieces of functionality that are not MMBase objects.
 * e.g. Session, Mail, Upload and other functionality
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: Module.java,v 1.9 2002-04-13 11:07:53 michiel Exp $
 */
public interface Module {

    /**
     * Retrieves the CloudContext to which this module belongs
     */
    public CloudContext getCloudContext();

    /**
     * Retrieve the name of the module (in the default language defined in mmbaseroot.xml).
     */
    public String getName();

    /**
     * Retrieve the description of the module.
     */
    public String getDescription();

    /**
     * Runs the command with the given parameter(s).
     * @param command the command to run, i.e. "MESSAGE-UPDATE".
         * @param parameters the main parameter for the command. Depends on the command issued. Not all
         *      commands make use of this parameter.
     */
    public void process(String command, Object parameter);

    /**
     * Runs the command with the given parameter(s).
     * @param command the command to run, i.e. "MESSAGE-UPDATE".
         * @param parameters the main parameter for the command. Depends on the command issued. Not all
         *      commands make use of this parameter.
         * @param auxparameters additional parameters for this command.
     */
    public void process(String command, Object parameter, Hashtable auxparameters);

    /**
     * Runs the command with the given parameter(s).
     * @param command the command to run, i.e. "MESSAGE-UPDATE".
         * @param parameters the main parameter for the command. Depends on the command issued. Not all
         *      commands make use of this parameter.
         * @param auxparameters additional parameters for this command.
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     */
    public void process(String command, Object parameter, Hashtable auxparameters, ServletRequest req,  ServletResponse resp);

    /**
     * Retrieve info from a module based on a command string.
     * Similar to the $MOD command in SCAN.
     * @param command the info to obtain, i.e. "USER-OS".
     */
    public String getInfo(String command);

    /**
     * Retrieve info from a module based on a command string
     * Similar to the $MOD command in SCAN.
     * @param command the info to obtain, i.e. "USER-OS".
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     */
    public String getInfo(String command, ServletRequest req, ServletResponse resp);

    /**
     * Retrieve info (as a list of virtual nodes) from a module based on a command string.
     * Similar to the LIST command in SCAN.
     * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
     * @param command the info to obtain, i.e. "USER-OS".
     * @param fields The names for the fields to retrieve. This allows a user to set it's own names for the fields (i.e.
     *      when calling the COLORS List command of the info module, this could be "RGB,name".
     *      If the number of names do not match the number of items returns, any additional items are numbered according to their position,
     *      given names such as "item1".
     * @param parameters a hashtable containing the named parameters of the list.
     */
    public NodeList getList(String command, Hashtable parameters);

    /**
     * Retrieve info from a module based on a command string
     * Similar to the LIST command in SCAN.
     * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
     * @param command the info to obtain, i.e. "USER-OS".
     * @param parameters a hashtable containing the named parameters of the list.
     * @param fields The names for the fields to retrieve. This allows a user to set it's own names for the fields (i.e.
     *      when calling the COLORS List command of the info module, this could be "RGB,name".
     *      If the number of names do not match the number of items returns, any additional items are numbered according to their position,
     *      given names such as "item1".
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     */
    public NodeList getList(String command, Hashtable parameters, ServletRequest req, ServletResponse resp);


}
