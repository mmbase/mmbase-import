/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.bridge.implementation.BasicCloudContext;

/**
 * The collection of clouds, and modules within the current application context.
 * Actually, there is always only <em>one</em> MMBase cloud (named 'mmbase'). There can however be
 * more modules (of which 'mmbaseroot' it one).
 *
 * This class is based on 'BasicCloudContext' because it itself want to be in the package
 * org.mmbase.bridge, but it wants to expose some members to other classes in the
 * org.mmbase.bridge.implementation package.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public final class LocalContext extends BasicCloudContext {

    private static LocalContext thisContext;

    /**
     * Constructor to call from the MMBase class
     * (private, so cannot be reached from a script)
     */
    private LocalContext() {
        super();
    }

    /**
     * Called from the script to retrieve the current CloudContext
     * @return current CloudContext
     */
    public static LocalContext getCloudContext() {
        if (thisContext == null)  thisContext = new LocalContext();
        return thisContext;
    }

    public String getUri() {
        return ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME;
    }

}
