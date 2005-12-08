/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.tests;
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

    int tryCount = 0;

    protected Cloud getCloud() {
        Cloud c = null;
        while(true) {
            CloudContext cc = null;
            try {
                cc = ContextProvider.getDefaultCloudContext();        
                c = cc.getCloud("mmbase", "class", null);
                break;
            } catch (BridgeException be) {
                if (cc instanceof LocalContext) {
                    throw be;
                }
                System.out.println(be.getMessage() + ". Perhaps mmbase not yet running, retrying in 5 seconds (" + tryCount + ")");
                try {
                    tryCount ++;
                    Thread.sleep(5000);
                } catch (Exception ie) {}
                if (tryCount > 25) throw be;
            }
        }
        return c;
    }

    protected Cloud getRemoteCloud() {
        return getRemoteCloud("rmi://127.0.0.1:1221/remotecontext");
    }

    protected Cloud getRemoteCloud(String uri) {
        Cloud c = null;
        while(true) {
            try {
                c =   ContextProvider.getCloudContext(uri).getCloud("mmbase", "class", null);
                break;
            } catch (BridgeException be) {
                System.out.println(be.getMessage() + ". Perhaps mmbase not yet running, retrying in 5 seconds");
                try {
                    tryCount ++;
                    if (tryCount > 25) throw be;
                    Thread.sleep(5000);
                } catch (Exception ie) {}
            }
        }
        return c;

    }

}
