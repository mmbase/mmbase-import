package org.mmbase.streams;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;
import static org.mmbase.datatypes.Constants.*;
import org.mmbase.bridge.dummy.*;
import org.mmbase.streams.transcoders.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;
import org.mmbase.util.logging.*;

import org.mmbase.servlet.FileServlet;


/**
 * @author Michiel Meeuwissen
 */

public class CreateCachesTest {

    private final static DummyCloudContext cloudContext = new DummyCloudContext();
    private final static String FILE = "foo.input";
    private static File  testFile;

    public CreateCachesTest() {
    }

    @BeforeClass
    public static void setUp() throws Exception {
        //cloudContext.clear();
        /*
        cloudContext.addCore();
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("media"));
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("streams"));
        */

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

        {
            testFile = new File(FileServlet.getDirectory(), FILE);

            // touch te file, contents is not important.
            FileOutputStream fo = new FileOutputStream(testFile);
            fo.close();
        }

    }




    @Test
    public void node() {
        Cloud cloud = cloudContext.getCloud("mmbase");
        //NodeManager nm = cloud.getNodeManager("streamsources");
        //Node newSource = nm.createNode(); // Dummy not yet ready enough

    }


    @Test
    public  void test1() throws Exception {
        CreateCachesProcessor proc = new CreateCachesProcessor("dummycreatecaches_1.xml");
        proc.setCacheManagers("dummy");
        Cloud cloud = cloudContext.getCloud("mmbase");

        Node container = cloud.getNodeManager("container").createNode();
        container.commit();

        NodeManager nm = cloud.getNodeManager("dummy");

        Node newSource = nm.createNode();
        newSource.setNodeValue("mediafragment", container);
        newSource.setNodeValue("mediaprovider", container);
        newSource.commit();

        assertEquals(cloudContext, newSource.getCloud().getCloudContext());

        assertTrue("" + newSource + "  " + cloudContext.getNodes(), cloud.hasNode(newSource.getNumber()));

        newSource.setStringValue("url", FILE);


        int nodeCount = cloudContext.getNodes().size();

        CreateCachesProcessor.Job job = proc.createCaches(cloud, newSource.getNumber());
        newSource.commit();

        job.waitUntilReady();

        // 2 nodes should have been created
        assertEquals("" + cloudContext.getNodes(), nodeCount + 2, cloudContext.getNodes().size());




    }
}


