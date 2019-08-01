/*

  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.

  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import javax.servlet.ServletContext;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.mock.MockBuilderReader;
import org.mmbase.bridge.mock.MockCloudContext;
import org.mmbase.util.MMBaseContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class TreeHelperTest  {

    protected MockCloudContext getCloudContext() {
        return MockCloudContext.getInstance();
    }

    @BeforeClass
    public static void setup() throws Exception {
        ServletContext sx = mock(ServletContext.class);
        when(sx.getContext(anyString())).thenReturn(sx);
        MMBaseContext.init(sx);
        org.mmbase.util.logging.SimpleTimeStampImpl.configure(org.mmbase.bridge.util.TreeHelper.class.getName(), "debug");
        MockCloudContext cc =  MockCloudContext.getInstance();
        cc.addCore();
        cc.addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));

    }



    @Test
    public void sanity() throws Exception {
        Cloud cloud = getCloudContext().getCloud("mmbase", "class", null);
        TreeHelper helper = new TreeHelper();
        System.out.println(TreeHelper.htmlRoot.getResource("/"));
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
