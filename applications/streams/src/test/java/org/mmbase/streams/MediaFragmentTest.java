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
        int fragmentsBefore = Queries.count(getCloud().getNodeManager("mediafragments").createQuery());
        Node newSource = newNode();
        newSource.commit();
        assertTrue(newSource.getNumber() > 0);
        newSource.getStringValue("title");
        newSource.setStringValue("title", "test test");
        newSource.commit();

        assertEquals("test test", newSource.getStringValue("title"));
        assertNotNull(newSource.getNodeValue("mediafragment"));
        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));

        newSource.setNodeManager(newSource.getCloud().getNodeManager("videostreamsources"));
        newSource.commit();

        assertEquals("test test", newSource.getStringValue("title"));
        assertNotNull(newSource.getNodeValue("mediafragment"));

        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));
        assertEquals("videofragments", newSource.getNodeValue("mediafragment").getNodeManager().getName()); // FAILS!


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


