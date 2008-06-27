/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @deprecated is this (cacheversionfile) used? seems obsolete now
 * @author Daniel Ockeloen
 * @version $Id: VersionCacheNode.java,v 1.8 2008-06-12 11:25:10 michiel Exp $
 */
class VersionCacheNode {

    private static Logger log = Logging.getLoggerInstance(VersionCacheNode.class.getName());
    private MMObjectNode versionnode;
    private Vector<VersionCacheWhenNode> whens = new Vector<VersionCacheWhenNode>();
    private MMBase mmb;

    public VersionCacheNode(MMBase mmb) {
        this.mmb = mmb;
    }

    public void handleChanged(String buildername,int number) {
        // method checks if this really something valid
        // and we should signal a new version

        boolean dirty = false;
        for (VersionCacheWhenNode whennode : whens) {
            Vector<String> types = whennode.getTypes();

            // check if im known in the types part
            if (types.contains(buildername)) {
                // is there only 1 builder type ?
                if (log.isDebugEnabled()) log.debug("types="+types.toString());
                if (types.size() == 1) {
                    dirty = true;
                } else {
                    // so multiple prepare a multilevel !
                    Vector<String> nodes = whennode.getNodes();

                    Vector<String> fields = new Vector<String>();
                    fields.addElement(buildername + ".number");
                    Vector<String> ordervec = new Vector<String>();
                    Vector<String> dirvec = new Vector<String>();

                    Vector<MMObjectNode> vec = mmb.getClusterBuilder().searchMultiLevelVector(nodes,fields,"YES",types,buildername+".number=="+number,ordervec,dirvec);
                    if (log.isDebugEnabled()) log.debug("VEC=" + vec);
                    if (vec != null && vec.size() > 0) {
                        dirty = true;
                    }
                }
            }
        }

        if (dirty) {
            // add one to the version of this counter
            int version = versionnode.getIntValue("version");
            versionnode.setValue("version",version + 1);
            versionnode.commit();
            if (log.isDebugEnabled()) log.debug("Changed = "+(version+1));
        }
    }

    public void setVersionNode(MMObjectNode versionnode) {
        this.versionnode = versionnode;
    }

    public void addWhen(VersionCacheWhenNode when) {
        whens.addElement(when);
    }

}
