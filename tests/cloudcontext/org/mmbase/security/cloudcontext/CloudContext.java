/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.cloudcontext;

import org.mmbase.tests.BridgeTest;
import junit.framework.TestCase;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.Casting;
import org.w3c.dom.Document;
/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 */

public class CloudContext extends BridgeTest {

    static boolean started = false;

    public void setUp() throws Exception  {
        if (! started) {
            startMMBase();
            started = true;
        }
    }

    public void testImplementation() {
        assertEquals(org.mmbase.security.implementation.cloudcontext.Authenticate.class, 
                     getCloud().getCloudContext().getAuthentication().getClass());
        Cloud cloud = getCloud("foo");
        assertEquals("foo", cloud.getUser().getIdentifier());
    }


    public void testCreateRights() {
        Cloud cloud = getCloud("foo");
        assertFalse(cloud.getNodeManager("mmbasecontexts").mayCreateNode());
        assertTrue(cloud.getNodeManager("news").mayCreateNode());
        Node n1 = cloud.getNodeManager("mmbasecontexts").createNode();
        try {
            n1.commit();
            fail("Should not have been possible to create node of type 'mmbasecontexts', but it did not throw exception: " + n1);
        } catch(SecurityException se)  {
            // ok
        }
        Node n2 = cloud.getNodeManager("news").createNode();
        n2.commit();
        Node n3 = cloud.getNodeManager("news").createNode();
        n3.commit();
    }

    public void testWriteRights() {
        Cloud cloud = getCloud("foo");
        Node context = cloud.getNodeManager("mmbasecontexts").getList(null).get(0);
        Node news = cloud.getNodeManager("news").getList(null).get(0);
        assertFalse(context.mayWrite());
        assertTrue(news.mayWrite());
        try {
            context.setStringValue("name", "bla bla");
            fail("Should not have been been allowed to write in an mmbasecontext node");
        } catch (SecurityException se) {
            // ok
        }
        
        news.setStringValue("title", "blaa");
        news.commit();
        
    }

    public void testDeleteRights() {
        Cloud cloud = getCloud("foo");
        Node context = cloud.getNodeManager("mmbasecontexts").getList(null).get(0);
        Node news = cloud.getNodeManager("news").getList(null).get(0);
        assertFalse(context.mayDelete());
        assertTrue(news.mayDelete());
        try {
            context.delete(true);
            fail("Should not have been been allowed to delete an mmbasecontext node");
        } catch (SecurityException se) {
            // ok
        }
        
        news.delete(true);
        
    }


    public void testChangeContextRights() {
        Cloud cloud = getCloud("foo");
        Node context = cloud.getNodeManager("mmbasecontexts").getList(null).get(0);
        Node news = cloud.getNodeManager("news").getList(null).get(0);
        assertFalse(context.mayChangeContext());
        assertTrue(news.mayChangeContext());
        try {
            context.setContext("default");
            context.commit();
            fail("Should not have been been allowed to set context of an mmbasecontext node");
        } catch (SecurityException se) {
            // ok
        }
        
        news.setContext("admin");
        news.commit();
        // changed to a context which we may not change again
        assertFalse(news.mayChangeContext());                                               
        
    }

    public void testSetOwnPassord() {
        Cloud cloud = getCloud("foo");
        Node userNode = cloud.getNode(cloud.getCloudContext().getAuthentication().getNode(cloud.getUser()));
        assertEquals("foo", userNode.getStringValue("username"));        
        userNode.setStringValue("password", "bar2");
        userNode.commit();
    }
    public void testDeleteOwnNode() {
        Cloud cloud = getCloud("foo");
        Node userNode = cloud.getNode(cloud.getCloudContext().getAuthentication().getNode(cloud.getUser()));
        try {
            userNode.delete();
            fail("Should not have been been allowed to delete own node");
        } catch (SecurityException se) {
            // ok
        }       
    }


}

