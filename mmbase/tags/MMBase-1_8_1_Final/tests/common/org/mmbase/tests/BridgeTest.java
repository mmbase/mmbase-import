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
        while(c == null) {
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
        ensureDeployed(c, "local cloud");
        return c;
    }

    protected Cloud getRemoteCloud() {
        return getRemoteCloud("rmi://127.0.0.1:1221/remotecontext");
    }

    protected Cloud getRemoteCloud(String uri) {
        Cloud c = null;
        while(c == null) {
            try {
                c =   ContextProvider.getCloudContext(uri).getCloud("mmbase", "class", null);
                break;
            } catch (BridgeException be) {
                System.out.println(be.getMessage() + ". Perhaps mmbase '" + uri + "' not yet running, retrying in 5 seconds (" + tryCount + ")");
                try {
                    tryCount ++;
                    if (tryCount > 25) throw be;
                    Thread.sleep(5000);
                } catch (Exception ie) {}
            }
        }
        ensureDeployed(c, uri);
        return c;

    }

    protected void ensureDeployed(Cloud cloud, String uri) {
        while(true) {
            // make sure basic app is deployed
            if (cloud.hasRelationManager("bb", "cc", "posrel") &&
                cloud.hasRelationManager("aa", "bb", "related") &&
                cloud.hasRelationManager("bb", "aa", "related")
                ) {
                return;
            }
            if (!cloud.hasRelationManager("bb", "cc", "posrel")) {
                System.out.println("No relation bb--(posrel)-->cc, in '" + uri + "' perhaps BridgeTest application not yet deployed. Waiting another 5 seconds (" + tryCount + ")");
            } else if (!cloud.hasRelationManager("aa", "bb", "related")) {
                System.out.println("No relation aa--(related)-->bb, in '" + uri + "' perhaps BridgeTest application not yet deployed. Waiting another 5 seconds (" + tryCount + ")");
            } else if (!cloud.hasRelationManager("bb", "aa", "related")) {
                System.out.println("No relation bb--(related)-->aa, in '" + uri + "' perhaps BridgeTest application not yet deployed. Waiting another 5 seconds (" + tryCount + ")");
            }
            try {
                tryCount ++;
                Thread.sleep(5000);                
                if (tryCount > 25) {
                    System.err.println("Giving up");
                    return;
                };
            } catch (InterruptedException ie) {
                return;
            }
        }
    }
                
}
