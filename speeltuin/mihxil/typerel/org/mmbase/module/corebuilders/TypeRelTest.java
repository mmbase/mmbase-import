package org.mmbase.module.corebuilders;

import junit.framework.*;
import org.mmbase.bridge.*;
import java.util.*;

/**
 * JUnit tests for TypeRel
 *
 * @author  Michiel Meeuwissen 
 * @version $Id: TypeRelTest.java,v 1.6 2003-02-27 16:29:14 michiel Exp $
 */
public class TypeRelTest extends TestCase {

    static protected String UNIDIRROLE = "unidirectionalrelation";
    static protected String  BIDIRROLE = "bidirectionalrelation";
    static protected String  INHROLE   = "inheritancerelation";

    static protected Cloud cloud = null;
    static protected NodeManager relDefManager;
    static protected NodeManager typeRelManager;
    static protected NodeManager insRelManager; 
    static protected NodeManager newsManager; 
    static protected NodeManager urlsManager; 
    static protected NodeManager objectManager; 
    static protected NodeList    createdNodes;

    static protected Node        news;
    static protected Node        url;

    public TypeRelTest(String testName) {
        super(testName);
    }


    public void testListRelations() {       
        RelationManagerList rml = cloud.getRelationManagers();
        assertTrue(rml != null);
        assertTrue(rml.size() > 0);
        if (rml.size() == 0) {
            fail("cannot test");
        }
        
    }
    protected Node createRelDefNode(String role, int dir) {
        // create a new relation-definition
        Node reldef = relDefManager.createNode();
        reldef.setValue("sname", role);
        reldef.setValue("dname", role);
        reldef.setValue("sguiname", role);
        reldef.setValue("dguiname", role);
        reldef.setIntValue("dir", dir);
        reldef.setNodeValue("builder", insRelManager);
        reldef.commit();
        createdNodes.add(reldef);
        return reldef;
    }

    /**
     * Create bidirection relation type, and check if relationmanager in both directions can be found.
     */
    public void testBidirectionRelation() {               
        Node reldef = createRelDefNode(BIDIRROLE, 2);
        Node typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", newsManager);
        typerel.setNodeValue("dnumber", urlsManager);
        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();
        createdNodes.add(typerel);

        // now this relation must exist.

        RelationManagerList rml1 = cloud.getRelationManagers(newsManager, urlsManager, BIDIRROLE);
        assertTrue(rml1.size() > 0);

        RelationManager rm1 = rml1.getRelationManager(0);

        Relation r1 = rm1.createRelation(news, url);
        createdNodes.add(r1);
        // no exception should have occured.
       
        RelationManagerList rml2 = cloud.getRelationManagers(urlsManager, newsManager, BIDIRROLE);
        assertTrue(rml2.size() > 0);

        RelationManager rm2 = rml2.getRelationManager(0);
        Relation r2 = rm2.createRelation(url,  news);
        // no exception should have occured.
        createdNodes.add(r2);

        // using rm1 to create relation other direction should work?
        Relation r3 = rm1.createRelation(url,  news);
        createdNodes.add(r3);

                    
    }
    /*
     * Create unidirection relation type, and check if relationmanager in only one direction can be found.
     */

    public void testUnidirectionalRelation() {
        Node reldef = createRelDefNode(UNIDIRROLE, 1);

        Node typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", newsManager);
        typerel.setNodeValue("dnumber", urlsManager);

        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();
        createdNodes.add(typerel);


        // now this relation must exist.
        RelationManagerList rml1 = cloud.getRelationManagers(newsManager, urlsManager, UNIDIRROLE);
        assertTrue(rml1.size() > 0);

        RelationManager rm1 = rml1.getRelationManager(0);

        Relation r1 = rm1.createRelation(news, url);
        createdNodes.add(r1);

        try {
            Relation r2 = rm1.createRelation(url, news);
            fail("Should not have allowed unidirection relation the other way");
        } catch (BridgeException e) {
        }



        RelationManagerList rm2 = cloud.getRelationManagers(urlsManager, newsManager, UNIDIRROLE);
        assertTrue("Found the relations also the other way around, but it is unidirectional", rm2.size() == 0);

        RelationManager rm = rml1.getRelationManager(0);
    }

    public void testInheritanceRelations() {
        Node reldef = createRelDefNode(INHROLE, 2);

        Node typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", objectManager);
        typerel.setNodeValue("dnumber", urlsManager);
        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();
        createdNodes.add(typerel);

        // now this relation must exist.
        RelationManagerList rm1 = cloud.getRelationManagers(objectManager, urlsManager, INHROLE);
        assertTrue(rm1.size() > 0);

        RelationManagerList rm2 = cloud.getRelationManagers(newsManager, urlsManager, INHROLE);
        assertTrue(rm2.size() > 0);       
    }



    public void testClearUpMess() {
        System.out.println("Clearing up the mess");
        NodeIterator i = createdNodes.nodeIterator();
        while (i.hasNext()) { i.next();};

        while (i.hasPrevious()) {
            Node node = (Node) i.previous();
            System.out.println("Deleting " + node);
            node.delete();
        }
    }

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        if (cloud == null) {
            CloudContext cloudContext= ContextProvider.getCloudContext("rmi://127.0.0.1:1111/remotecontext");
            HashMap user = new HashMap();
            user.put("username", "admin");
            user.put("password", "admin5k");
            cloud = cloudContext.getCloud("mmbase","name/password",user);
            
            // needed builders for this test.
            try {
                relDefManager  = cloud.getNodeManager("reldef");
                typeRelManager = cloud.getNodeManager("typerel");
                insRelManager  = cloud.getNodeManager("insrel");
                newsManager    = cloud.getNodeManager("news");
                urlsManager    = cloud.getNodeManager("urls");
                objectManager  = cloud.getNodeManager("object");
            } catch (NotFoundException e) {
                throw new Exception("Test cases cannot be performed because " + e.getMessage() + " Please arrange this in your cloud before running this TestCase.");
             }

            createdNodes = cloudContext.createNodeList();
            assertNotNull("Could not create remotely a nodelist" , createdNodes);

            news = newsManager.createNode();
            news.setValue("title", "test node");
            news.commit();
            createdNodes.add(news);
            
            url = urlsManager.createNode();
            url.setValue("url", "http://url");
            url.commit();
            createdNodes.add(url);

        }
    }
    
}
    
