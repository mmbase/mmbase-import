/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.mmbar.endurancetests;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.mmbar.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author     Daniel Ockeloen
 * @created    February 28, 2005
 */
public class NodeBridgeEnduranceTest extends EnduranceTest {

    // logger
    private static Logger log = Logging.getLoggerInstance(NodeBridgeEnduranceTest.class);


    /**
     *  implementation of the test
     */
    public void testRun() {
        Cloud cloud = MMBarManager.getCloud();
        NodeManager nm = cloud.getNodeManager("mmpo_basicobject");
        if (nm == null) {
            log.error("Can't load nodemanager : " + nm + " from mmbase");
        } else {
            int count = getCount();
            ArrayList validlist = readValidNodes(nm, count);
            log.info("VALID LIST=" + validlist.size());
            long starttime = System.currentTimeMillis();
            int count2 = 0;
            try {
                for (currentpos = 0; currentpos < getCount(); currentpos++) {
                    Object o = cloud.getNode(((Integer) validlist.get(count2++)).intValue());
                    if (count2 > count) {
                        count2 = 0;
                    }
                }
            } catch (Exception e) {
                log.error("Error inside NodeBridgeEnduranceTest");
            }
            long endtime = System.currentTimeMillis();
            setResult(getCount(), endtime - starttime);
        }
    }


    /**
     *  get a list valid nodes we need for testing
     *
     * @param  nm     Description of the Parameter
     * @param  count  Description of the Parameter
     * @return        Description of the Return Value
     */
    public ArrayList readValidNodes(NodeManager nm, int count) {
        ArrayList validlist = new ArrayList();
        NodeQuery query = nm.createQuery();
        org.mmbase.bridge.NodeList result = nm.getList(query);
        NodeIterator i = result.nodeIterator();
        int count2 = 0;
        while (i.hasNext() && count > count2) {
            org.mmbase.bridge.Node n = (org.mmbase.bridge.Node) i.nextNode();
            validlist.add(new Integer(n.getNumber()));
            count2++;
        }
        return validlist;
    }

}

