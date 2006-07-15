package org.mmbase.bridge.tests;
import org.mmbase.bridge.*;
import java.io.*;

/**
 * JUnit tests for convertimage-interface implementation.
 
 * @author  Michiel Meeuwissen 
 * @version $Id: ConvertImageTest.java,v 1.2 2003-06-18 13:50:54 kees Exp $
 */
public class ConvertImageTest extends org.mmbase.tests.BridgeTest {
    private final static String JPG_IMAGE_NAME = "testimage.jpg";

    public void testImportJpegImage() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("images");
        Node jpegNode = nodeManager.createNode();
        jpegNode.setStringValue("title", JPG_IMAGE_NAME);
        byte[] bytes = getTextImageBytes(JPG_IMAGE_NAME);
        jpegNode.setByteValue("handle", bytes);
        jpegNode.commit();
        jpegNode.createAlias("jpeg.test.image");
    }

    public void testImportedJpegImage() {
        Cloud cloud = getCloud();
        Node node = cloud.getNode("jpeg.test.image");
        byte[] bytes = node.getByteValue("handle");
        assertTrue("MMBase failed to determine mime-type properly (magicfile problem?)", node.getStringValue("itype").equals("jpeg"));
        node.delete();
    }

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        startMMBase();
        startLogging();
    }

    /**
     * read the test image from the file system and return it a byte[]
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
        } catch (IOException ioe) {}
        return null;
    }
}
