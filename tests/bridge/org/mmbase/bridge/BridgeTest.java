/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import junit.framework.*;


/**
 * @author Michiel Meeuwissen
 */
public abstract class BridgeTest extends TestCase {

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

    static protected void startMMBase() throws Exception {
        
        org.mmbase.module.core.MMBaseContext.init();
        org.mmbase.module.core.MMBase.getMMBase();


    }

}
