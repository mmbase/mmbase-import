package org.mmbase.bridge.tests;

import org.mmbase.bridge.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.images.*;

import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;

/**
 * JUnit tests for convertimage-interface implementation.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ConvertImageTest.java,v 1.6 2008-09-23 07:58:54 michiel Exp $
 */
public class ConvertImageTest extends org.mmbase.tests.BridgeTest {

    private final static String JPG_IMAGE_NAME = "testimage.jpg";

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
        node.getIntValue("cache(s(30x30))"); // does this actually convert something, I think not.
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
        p.set("template","s(30x30)");
        f.getFunctionValue(p);
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
        Factory.init(parameters);
    }

    public void testFailAnImage() {
        Map<String, String> originalParameters = breakImaging();

        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        Function f = node.getFunction("cache");
        Parameters p = f.createParameters();
        p.set("template","s(31x31)");
        Object icache = f.getFunctionValue(p);

        restoreImaging(originalParameters);


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
        NodeManager nodeManager = cloud.getNodeManager("images");
        Node jpegNode = nodeManager.createNode();
        jpegNode.setStringValue("title", JPG_IMAGE_NAME);
        byte[] bytes = getTextImageBytes(JPG_IMAGE_NAME);
        jpegNode.setByteValue("handle", bytes);
        jpegNode.commit();
        jpegNode.createAlias("jpeg.test.image");
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        node.delete();
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
