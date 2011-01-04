/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.datatypes.*;
import org.mmbase.storage.search.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
   *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class CloneUtilTest {

    private static final String REMOTE_URI = "rmi://127.0.0.1:1111/exampleremotecontext";
    private static Cloud remoteCloud;


    @BeforeClass
    public static void setup() throws Exception {
        //MockCloudContext.getInstance().addCore();
        //MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
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
    public void cloneRelation() {
        assumeNotNull(remoteCloud);
        Node mag = remoteCloud.getNode("default.mags");

        {
            Relation rel = mag.getRelations("posrel", "news").get(0);
            Node news = rel.getDestination();
            assertEquals("news", news.getNodeManager().getName());

            Node clone = CloneUtil.cloneNode(news);
            assertEquals("news", clone.getNodeManager().getName());
            clone.commit();
            int number = clone.getNumber();
            System.out.println("Created " + number);
            assertTrue(number > 0);
            Relation relClone = CloneUtil.cloneRelation(rel);
            relClone.setDestination(clone);
            assertEquals(number, relClone.getDestination().getNumber());
            relClone.commit();
            assertEquals(number, relClone.getDestination().getNumber());
        }
        {
            Node clone = CloneUtil.cloneNode(mag);
            clone.setValue("title", "clone of " + mag.getStringValue("title"));
            clone.commit();
            System.out.println("Created " + clone.getNumber());
            RelationIterator news = mag.getRelations("posrel", remoteCloud.getNodeManager("news"), "destination").relationIterator();
            while (news.hasNext()) {
                Relation rel = news.nextRelation();
                Relation relclone = CloneUtil.cloneRelation(rel);
                relclone.setSource(clone);
                assertEquals(relclone.getIntValue("snumber"),  clone.getNumber());
                relclone.commit();
                assertEquals(relclone.getIntValue("snumber"), clone.getNumber());
                System.out.println("Created " + relclone.getFunctionValue("gui", null));
            }
            assertEquals(mag.countRelations(), clone.countRelations());
        }
    }
}
