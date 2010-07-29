/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import org.mmbase.datatypes.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: SetFunctionTest.java 39514 2009-11-03 15:51:57Z michiel $
 */
public class NodeFunctionTest {

    private static final String REMOTE_URI = "rmi://127.0.0.1:1111/exampleremotecontext";
    private static Cloud remoteCloud;


    @BeforeClass
    public static void setup() throws Exception {
        try {
            CloudContext c =  ContextProvider.getCloudContext(REMOTE_URI);
            remoteCloud = c.getCloud("mmbase", "class", null);
            System.out.println("Found remote cloud " + remoteCloud);
        } catch (Exception e) {
            System.err.println("Cannot get RemoteCloud. (" + e.getMessage() + "). Some tests will be skipped. (but reported as succes: see http://jira.codehaus.org/browse/SUREFIRE-542)");
            System.err.println("You can start up a test-environment for remote tests: trunk/example-webapp$ mvn jetty:run");
            remoteCloud = null;
        }
    }


    @Test
    public void age() {
        assumeNotNull(remoteCloud);
        Node node = remoteCloud.getNode("default.mags");
        int age = node.getFunctionValue("age", null).toInt();
        System.out.println(node.getNumber() + " age: " + age + " days");
        assertNotNull(age);
    }


    @Test
    public void gui() {
        assumeNotNull(remoteCloud);
        NodeManager images = remoteCloud.getNodeManager("images");
        Node image = images.getList(null).getNode(0);
        assertNotNull(image.getFunctionValue("gui", null).get());
        assertTrue(image.getFunctionValue("servletpath", null).toString().indexOf("" + image.getNumber()) > 0);
        assertTrue(image.getFunctionValue("url", null).toString().indexOf("" + image.getNumber()) > 0);
        System.out.println("icon for " +  image.getNumber() + ": " + image.getFunctionValue("iconurl", null).get());
        Function servletPath = image.getFunction("servletpath");
        assertNotNull(servletPath.getFunctionValue(null));
        assertTrue(servletPath.getFunctionValue(null).toString().indexOf("" + image.getNumber()) > 0);
        Function url = image.getFunction("url");
        assertTrue(url.getFunctionValue(null).toString().indexOf("" + image.getNumber()) > 0);

    }




}
