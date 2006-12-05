/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.mmbar.writetests;

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
public class NodeBridgeWriteTest extends WriteTest {

    // logger
    private static Logger log = Logging.getLoggerInstance(NodeBridgeWriteTest.class);


    /**
     *  A unit test for JUnit
     */
    public void testRun() {
        NodeManager nm = MMBarManager.getCloud().getNodeManager("mmpo_basicobject");
        if (nm == null) {
            log.error("Can't load nodemanager : " + nm + " from mmbase");
        } else {
 	    int realcount =  getCount() / getThreads();
            try {
                for (int i = 0; i < realcount; i++) {
                    org.mmbase.bridge.Node node = nm.createNode();
		    currentpos++;
                    node.setStringValue("name", "name" + currentpos);
                    node.setStringValue("description", "description" + currentpos);
                    node.commit();
                }
            } catch (Exception e) {
                log.error("Error inside NodeBridgeWriteTest");
            }
        }
    }

}

