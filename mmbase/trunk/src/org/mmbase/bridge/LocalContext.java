/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.bridge.implementation.BasicCloudContext;

/**
 * The collection of clouds, and modules within a Java Virtual Machine.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: LocalContext.java,v 1.6 2006-11-11 19:26:03 michiel Exp $
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

}
