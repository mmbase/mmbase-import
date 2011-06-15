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

package org.mmbase.streams.createcaches;

import org.junit.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.MockCloudContext;
import org.mmbase.bridge.util.Queries;

import java.io.*;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;


/**
 * @author Michiel Meeuwissen
 */

public class CreateCachesTest {

    private static final String REMOTE_URI = "rmi://127.0.0.1:1111/remotecontext";
    private static Cloud remoteCloud;


    private final static MockCloudContext cloudContext = new MockCloudContext();
    private final static String FILE = "basic.mp4";
    private static File  testFile;

    public CreateCachesTest() {
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
            System.err.println("You can start up a test-environment for remote tests: toip/trunk$ mvn jetty:run");
            remoteCloud = null;
        }

        //cloudContext.clear();
        /*
        cloudContext.addCore();
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("media"));
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("streams"));
        */

        /* Mock stuff not yet sufficiently useable
        {
            Map<String, DataType> map = new HashMap<String, DataType>();
            map.put("number",    DATATYPE_INTEGER);
            map.put("url",       DATATYPE_STRING);
            map.put("title",     DATATYPE_STRING);
            map.put("mimetype",  DATATYPE_STRING);
            map.put("id",        DATATYPE_INTEGER);
            map.put("key",       DATATYPE_STRING);
            map.put("format",    DATATYPE_INTEGER);
            map.put("codec",     DATATYPE_INTEGER);
            map.put("state",     DATATYPE_INTEGER);
            map.put("mediafragment",     DATATYPE_NODE);
            map.put("mediaprovider",     DATATYPE_NODE);
            cloudContext.addNodeManager("dummy", map);

            cloudContext.getCloud("mmbase").getNodeManager("dummy").getProperties().put("org.mmbase.streams.cachestype", "dummy");

        }

        {
            Map<String, DataType> map = new HashMap<String, DataType>();
            map.put("number", DATATYPE_INTEGER);
            map.put("title",     DATATYPE_STRING);
            cloudContext.addNodeManager("container", map);
        }

        */
        testFile = new File("samples", FILE);

    }

    protected Cloud getCloud() {
        if (remoteCloud != null) {
            remoteCloud.setProperty(Processor.NOT, "no implicit processesing please");
            //remoteCloud.setProperty(BinaryCommitProcessor.NOT, "no implicit processesing please");
            //remoteCloud.setProperty(org.mmbase.applications.media.FragmentTypeFixer.NOT, "no implicit processesing please");
        }
        assumeNotNull(remoteCloud);
        return remoteCloud;
    }




    @Test
    public void node() {
        Cloud cloud = getCloud();
        assumeNotNull(cloud);
        NodeManager nm = cloud.getNodeManager("streamsources");
        assumeNotNull(nm);
        Node newSource = nm.createNode(); //
        newSource.commit();

    }

    Node getNode(File dir) throws Exception {
        Cloud cloud = getCloud();
        assumeNotNull(cloud);


        Node container = cloud.getNodeManager("mediafragments").createNode();
        container.commit();

        NodeManager nm = cloud.getNodeManager("streamsources");


        System.out.println("DIR " + dir);

        Node newSource = nm.createNode();
        newSource.setNodeValue("mediafragment", container);
        newSource.setNodeValue("mediaprovider", cloud.getNode("default.provider"));
        newSource.commit();

        File tempFile = new File(dir, newSource.getNumber() + getClass().getName() + "." + FILE);
        if (! tempFile.exists()) {
            org.mmbase.util.IOUtil.copy(new FileInputStream(testFile), new FileOutputStream(tempFile));
        }
        assertEquals(513965, tempFile.length());


        assertTrue(testFile.exists());


        newSource.setValueWithoutProcess("url", tempFile.getName());
        newSource.commit();
        return newSource;
    }

    Processor get(String config) {
        Cloud cloud = getCloud();
        assumeNotNull(cloud);

        NodeManager nm = cloud.getNodeManager("streamsources");
        Processor proc = new Processor(config);
        File dir = new File(nm.getFunctionValue("fileServletDirectory", null).toString());
        proc.setDirectory(dir);
        return proc;
    }

    Node refresh(Node source) throws InterruptedException {
        return source.getCloud().getNode(source.getNumber());

    }
    void checkSource(Node source, int sourceCount) {
        checkSource(source, sourceCount, "");
    }
    void checkSource(Node source, int sourceCount, String message) {
        assertEquals(source.getNumber() + " " + message, "videostreamsources", source.getNodeManager().getName());
        assertTrue(source.getNumber() + " " + message, source.getStringValue("mimetype").startsWith("video/"));
        assertNotNull(source.getNumber() + " " + message, source.getValue("width"));
        assertNotNull(source.getValue("height"));
        assertEquals(352, source.getIntValue("width"));
        assertEquals(288, source.getIntValue("height"));
        assertEquals(1, source.getRelatedNodes("mediaproviders").size());
        assertEquals(1, source.getRelatedNodes("mediafragments").size());

        Node mediafragment = source.getNodeValue("mediafragment");
        assertEquals("videofragments", mediafragment.getNodeManager().getName());
        //assertEquals("" + mediafragment.getNumber() + " is supposed to have " + sourceCount + " source", sourceCount, mediafragment.getRelatedNodes("mediasources").size()); // why does this not work?
        assertEquals("" + mediafragment.getNumber() + " is supposed to have " + sourceCount + " source", sourceCount, mediafragment.getRelatedNodes("object", "related", "destination").size());
    }

    @Test
    public void crazy() throws Exception  {
        for (int i = 0; i < 10; i++) {
            Processor proc = get("crazycreatecaches.xml");
            Node source = getNode(proc.getDirectory());
            Job job = proc.createCaches(source.getCloud(), source.getNumber());
            job.waitUntil(Stage.READY);
            assertTrue("During test " + i +". No node " + source.getNumber() + " in " + source.getCloud(), source.getCloud().hasNode(source.getNumber()));
            source = refresh(source);
        }
    }


    @Test
    public void recognizerOnly() throws Exception  {
        Processor proc = get("dummycreatecaches_0.xml");
        Node source = getNode(proc.getDirectory());
        Job job = proc.createCaches(source.getCloud(), source.getNumber());

        job.waitUntil(Stage.READY);

        assertTrue("No node " + source.getNumber() + " in " + source.getCloud(), source.getCloud().hasNode(source.getNumber()));
        source = refresh(source);
        checkSource(source, 1);
    }

    @Test
    public  void simple() throws Exception {
        int fragmentsBefore = Queries.count(getCloud().getNodeManager("mediafragments").createQuery());
        Processor proc = get("dummycreatecaches_1.xml");
        Node source = getNode(proc.getDirectory());
        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));
        Job job = proc.createCaches(source.getCloud(), source.getNumber());
        source.commit();
        job.waitUntil(Stage.READY);
        assertTrue(source.getCloud().hasNode(source.getNumber()));
        source = refresh(source);
        checkSource(source, 2);
        assertEquals(fragmentsBefore + 1, Queries.count(getCloud().getNodeManager("mediafragments").createQuery()));
    }


    @Test
    public  void twoSteps() throws Exception {
        Processor proc = get("dummycreatecaches_2.xml");
        Node source = getNode(proc.getDirectory());
        Job job = proc.createCaches(source.getCloud(), source.getNumber());
        source.commit();
        job.waitUntil(Stage.READY);
        source = refresh(source);
        checkSource(source, 3);
    }

    @Test
    public  void twoStepsTwoResults() throws Exception {
        Processor proc = get("dummycreatecaches_3.xml");
        Node source = getNode(proc.getDirectory());
        Job job = proc.createCaches(source.getCloud(), source.getNumber());
        source.commit();
        job.waitUntil(Stage.READY);
        source = refresh(source);
        checkSource(source, 4);
    }

    @Test
    public  void ignoreByMimeType() throws Exception {
        Processor proc = get("dummycreatecaches_4.xml");
        Node source = getNode(proc.getDirectory());
        Job job = proc.createCaches(source.getCloud(), source.getNumber());
        source.commit();
        job.waitUntil(Stage.READY);
        source = refresh(source);
        checkSource(source, 1);
    }

    @Test
    public  void twoStepsTwoResultsIgnoreAudio() throws Exception {
        Processor proc = get("dummycreatecaches_5.xml");
        Node source = getNode(proc.getDirectory());
        Job job = proc.createCaches(source.getCloud(), source.getNumber());
        source.commit();
        job.waitUntil(Stage.READY);
        source = refresh(source);
        checkSource(source, 4);
    }
}


