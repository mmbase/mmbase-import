/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.mmbar.readtests;

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
 * @created    March 4, 2005
 */
public class NodeHttpReadTest extends ReadTest {

    // logger
    private static Logger log = Logging.getLoggerInstance(NodeHttpReadTest.class);
    private ArrayList validlist;
    private int validcount;

    public void initTest() {
        Cloud cloud = MMBarManager.getCloud();
        String buildername =  getProperty("builder");
        if (buildername==null) buildername = "mmpo_basicobject";
        NodeManager nm = cloud.getNodeManager(buildername);
        if (nm == null) {
            log.error("Can't load nodemanager : " + buildername + " from mmbase");
        } else {
	    if (validlist==null) {
                validcount = getCount() / getThreads();
                validlist = readValidNodes(nm, validcount);
	        validcount = validlist.size();
	    }
	}
    }

    /**
     * 
     */
    public void testRun() {
        Cloud cloud = MMBarManager.getCloud();
        String buildername =  getProperty("builder");
	if (buildername==null) buildername = "mmpo_basicobject";
        NodeManager nm = cloud.getNodeManager(buildername);
        if (nm == null) {
            log.error("Can't load nodemanager : " + buildername + " from mmbase");
        } else {

            int count2 = 0;
            try {
	        int realcount = getCount() / getThreads();
                for (int i = 0; i < realcount; i++) {
		    URL url = new URL(MMBarManager.getBaseTestUrl()+"/reading/NodeHttpReadTest.jsp?nodeid="+validlist.get(count2++));
		    getURLBytes(url);
		    currentpos++;
                    if (count2 >= validcount) {
                        count2 = 0;
                    }
                }
            } catch (Exception e) {
                log.error("Error inside NodeHttpReadTest");
		e.printStackTrace();
            }
        }
    }


    /**
     *  Description of the Method
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

