/*

  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.

  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.util.ResourceLoader;
import java.util.*;
import org.junit.*;
import org.springframework.mock.web.*;
import org.springframework.core.io.*;
import javax.servlet.ServletContext;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: QueriesTest.java 41584 2010-03-23 18:05:29Z michiel $
 */
public class TreeHelperTest  {

    protected MockCloudContext getCloudContext() {
        return MockCloudContext.getInstance();
    }

    @BeforeClass
    public static void setup() throws Exception {
        ServletContext sx = new MockServletContext("/src/test/files/treetests",  new FileSystemResourceLoader()) {
                @Override
                public ServletContext getContext(String uriPath) {
                    return this;
                }
            };
        org.mmbase.module.core.MMBaseContext.init(sx);
        org.mmbase.util.logging.SimpleTimeStampImpl.configure(org.mmbase.bridge.util.TreeHelper.class.getName(), "debug");
        MockCloudContext cc =  MockCloudContext.getInstance();
        cc.addCore();
        cc.addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));

    }



    @Test
    public void sanity() throws Exception {
        Cloud cloud = getCloudContext().getCloud("mmbase", "class", null);
        TreeHelper helper = new TreeHelper();
        System.out.println(helper.htmlRoot.getResource("/"));
    }
    @Test
    public void findLeafFile() throws Exception {
        Cloud cloud = getCloudContext().getCloud("mmbase", "class", null);
        Node node = cloud.getNodeManager("news").createNode();
        node.setStringValue("title", "foo");
        node.commit();
        TreeHelper helper = new TreeHelper();
        helper.setCloud(cloud);
        assertEquals("test", helper.findLeafFile("test", null, null));
        assertEquals("test?a=a a", helper.findLeafFile("test?a=a a", null, null));
        assertEquals("", helper.findLeafFile("test", "50000", null));
        //assertEquals("/" + node.getNumber() + "/test", helper.findLeafFile("test", "" + node.getNumber(), null)); // fails?
    }


}
