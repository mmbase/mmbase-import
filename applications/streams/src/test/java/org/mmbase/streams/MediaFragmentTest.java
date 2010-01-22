/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.DataType;
import static org.mmbase.datatypes.Constants.*;
import org.mmbase.datatypes.processors.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.streams.transcoders.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;
import org.mmbase.util.logging.*;

import org.mmbase.servlet.FileServlet;


/**
 * @author Michiel Meeuwissen
 */

public class MediaFragmentTest {

    private static final String REMOTE_URI = "rmi://127.0.0.1:1111/remotecontext";
    private static Cloud remoteCloud;


    private final static MockCloudContext cloudContext = new MockCloudContext();
    private final static String FILE = "basic.mp4";
    private static File  testFile;

    public MediaFragmentTest() {
    }


    @AfterClass
    public static void shutdown() {
        System.out.println("Ready testing ");
        org.mmbase.util.ThreadPools.shutdown();
    }
    @BeforeClass
    public static void setUp() throws Exception {
        try {
            CloudContext c =  ContextProvider.getCloudContext(REMOTE_URI);
            remoteCloud = c.getCloud("mmbase", "class", null);
            System.out.println("Found remote cloud " + remoteCloud);
        } catch (Exception e) {
            System.err.println("Cannot get RemoteCloud. (" + e.getMessage() + "). Some tests will be skipped. (but reported as succes: see http://jira.codehaus.org/browse/SUREFIRE-542)");
            System.err.println("You can start up a test-environment for remote tests: trunk/example-webapp$ mvn jetty:run");
            remoteCloud = null;
        }
        testFile = new File("samples", FILE);

    }

    protected Cloud getCloud() {
        if (remoteCloud != null) {
            remoteCloud.setProperty(org.mmbase.streams.createcaches.Processor.NOT, "no implicit processesing please");
            //remoteCloud.setProperty(BinaryCommitProcessor.NOT, "no implicit processesing please");
            //remoteCloud.setProperty(org.mmbase.applications.media.FragmentTypeFixer.NOT, "no implicit processesing please");
        }
        assumeNotNull(remoteCloud);
        return remoteCloud;
    }


    Node newNode() {
        Cloud cloud = getCloud();
        return newNode(cloud);
    }

    Transaction getTransaction() {
        Cloud cloud = getCloud();
        assumeNotNull(cloud);
        return cloud.getTransaction(getClass().getName());
    }

    Node newNode(Cloud cloud) {
        NodeManager nm = cloud.getNodeManager("streamsources");
        assumeNotNull(nm);
        Node newSource = nm.createNode(); //
        return newSource;
    }


    @Test
    public void commitGetSetCommit() {
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) System.out.print(".");
        int fragmentsBefore = Queries.count(getCloud().getNodeManager("mediafragments").createQuery());
        Node newSource = newNode();
        newSource.commit();
        assertTrue(newSource.getNumber() > 0);
        newSource.getStringValue("title");
        newSource.setStringValue("title", "test test");
        newSource.commit();

        assertEquals("test test", newSource.getStringValue("title"));
        assertNotNull(newSource.getNodeValue("mediafragment"));
        final int mediaFragment = newSource.getNodeValue("mediafragment").getNumber();

        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));

        newSource.setNodeManager(newSource.getCloud().getNodeManager("videostreamsources"));

        assertNotNull(newSource.getNodeValue("mediafragment"));
        newSource.commit();
        //newSource.commit(); // ADDING THIS FIXES THE FAIL!

        assertEquals("test test", newSource.getStringValue("title"));
        //newSource.setStringValue("title", "test test test");
        //newSource.commit();

        assertNotNull(newSource.getNodeValue("mediafragment"));
        assertEquals(mediaFragment, newSource.getNodeValue("mediafragment").getNumber());

        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));
        assertEquals("videofragments", newSource.getCloud().getNode(mediaFragment).getNodeManager().getName());
        assertEquals("videofragments", newSource.getNodeValue("mediafragment").getNodeManager().getName()); // FAILS!

        assertEquals("test test", newSource.getNodeValue("mediafragment").getStringValue("title"));
        assertEquals("test test", newSource.getStringValue("title"));
        assertEquals("test test", newSource.getCloud().getNode(mediaFragment).getStringValue("title"));
        }

    }



    // making relations to non-existing nodes is failing
    //@Test
    public void commitSetCommitInTranaction() {
        Transaction t = getTransaction();
        Node newSource = newNode(t);
        newSource.setStringValue("title", "test test");
        newSource.commit();
        t.commit();
        assertEquals("test test", newSource.getStringValue("title"));

    }

    // making relations to non-existing nodes is failing
    //@Test
    public void commitSetCommit() {
        int fragmentsBefore = Queries.count(getCloud().getNodeManager("mediafragments").createQuery());
        Node newSource = newNode();
        newSource.setStringValue("title", "test test");
        newSource.commit();
        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));
        assertEquals("test test", newSource.getStringValue("title"));

    }
}


