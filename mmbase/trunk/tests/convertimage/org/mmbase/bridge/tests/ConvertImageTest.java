package org.mmbase.bridge.tests;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import java.io.*;
import java.util.*;
import org.mmbase.util.images.*;

import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;

/**
 * JUnit tests for convertimage-interface implementation.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ConvertImageTest.java,v 1.7 2008-09-24 06:14:58 michiel Exp $
 */
public class ConvertImageTest extends org.mmbase.tests.BridgeTest {

    private final static String JPG_IMAGE_NAME = "testimage.jpg";

    protected int countIcaches() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("icaches");
        return Queries.count(nodeManager.createQuery());
    }


    public void testImportedJpegImage() {
        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        node.getByteValue("handle");
        assertTrue("MMBase failed to determine mime-type properly (magicfile problem?)", node.getStringValue("itype").equals("jpeg"));
        //node.delete();
    }

    /**
     * test if an image can be converted using the getIntValue
     */
    public void testGetInvalueCachedImage() {
        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        int icacheNodeNumber = node.getIntValue("cache(s(30x30))");

        assertEquals(1, countIcaches());
        Node icache = cloud.getNode(icacheNodeNumber);
        icache.getFunctionValue("wait", null); // triggers actual conversion
        assertEquals(1, countIcaches());
    }

    /**
     * test if an image can be converted using getFunctionValue
     *
     */
    public void testFunctionValueCachedImage() {
        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        Function f = node.getFunction("cache");
        Parameters p = f.createParameters();
        p.set("template","s(31x31)");
        int nn = (Integer) f.getFunctionValue(p);
        assertEquals(2, countIcaches());
        Node icache = cloud.getNode(nn);
        icache.getFunctionValue("wait", null);
        assertEquals(2, countIcaches());
    }

    protected Map<String, String> breakImaging() {
        Map<String, String> originalParameters = Factory.getParameters();
        Factory.shutdown();
        Map<String, String> brokenParameters = new HashMap<String, String>();
        brokenParameters.putAll(originalParameters);
        brokenParameters.put("ImageConvert.ConverterCommand", "nonexistingbinary");
        Factory.init(brokenParameters);
        return originalParameters;
    }

    protected void restoreImaging(Map<String, String> parameters) {
        Factory.shutdown();
        System.err.println("Restoring with " + parameters);
        Factory.init(parameters);
    }

    public void testFailAnImage() {
        // MMB-495
        Map<String, String> originalParameters = breakImaging();

        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        Function f = node.getFunction("cache");
        Parameters p = f.createParameters();
        p.set("template","s(32x32)");
        int icacheNumber = (Integer) f.getFunctionValue(p);
        assertEquals(3, countIcaches());
        assertTrue(icacheNumber > 0);
        Node icache = cloud.getNode(icacheNumber);
        icache.getFunctionValue("wait", null); // this should fail.
        assertTrue(icache.isNull("handle"));
        assertEquals(3, countIcaches());

        restoreImaging(originalParameters);

        assertEquals(new Integer(icacheNumber), (Integer) f.getFunctionValue(p));
        assertEquals(3, countIcaches());

        icache.getFunctionValue("wait", null); // should succeed now.
        assertEquals(3, countIcaches());
        assertFalse(icache.isNull("handle"));


    }
    private static Cloud cloud;
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        if (cloud == null) {
            startMMBase();
            startLogging();
            cloud = getCloud();
        }
        if (! cloud.hasNode("jpeg.test.image")) {

            NodeManager nodeManager = cloud.getNodeManager("images");
            Node jpegNode = nodeManager.createNode();
            jpegNode.setStringValue("title", JPG_IMAGE_NAME);
            byte[] bytes = getTextImageBytes(JPG_IMAGE_NAME);
            jpegNode.setByteValue("handle", bytes);
            jpegNode.commit();
            jpegNode.createAlias("jpeg.test.image");
        }
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        Cloud cloud = getCloud();
        //Node node = cloud.getNode("jpeg.test.image");
        //node.delete();
    }
    /**
     * read the test image from the file system and return it a byte[]
     *
     * @return the byte[] containing the image
     */
    private byte[] getTextImageBytes(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[200];
            int readSize = 0;
            while ((readSize = in.read(buffer)) > 0) {
                baos.write(buffer, 0, readSize);
            }
            return baos.toByteArray();
        } catch (IOException ioe) {
            fail("did not find image to load " + this.getClass().getResource(name));
        }
        return null;
    }
}
