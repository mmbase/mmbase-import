/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.tests;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * Test-case running via the bridge. This base class takes care of
 * configuration issues like login-information and port-numbers.
 *
 * @author Michiel Meeuwissen
 */
public abstract class BridgeTest extends MMBaseTest {

    public BridgeTest() {
        super();
    }
    public BridgeTest(String name) {
        super(name);
    }

    protected Map getNamePassword() {
        Map user = new HashMap();
        user.put("username", "admin");
        user.put("password", "admin2k");
        return user;
    }

    protected Cloud getCloud() {
        return getLocalCloud();
    }

    protected Cloud getLocalCloud() {    
        return  ContextProvider.getDefaultCloudContext().getCloud("mmbase", "name/password", getNamePassword());
    }

    protected Cloud getRemoteCloud() {
        return  ContextProvider.getCloudContext("rmi://127.0.0.1:1221/remotecontext").getCloud("mmbase","name/password", getNamePassword());
    }

}
