package org.mmbase.module.corebuilders;

import junit.framework.*;
import org.mmbase.bridge.*;
import java.util.*;

/**
 * JUnit tests for TypeRel
 *
 * @author  Michiel Meeuwissen 
 * @version $Revision: 1.2 $
 */
public class TypeRelTest extends TestCase {

    static protected String UNIDIRROLE = "unidirectionalrelation";
    static protected String  BIDIRROLE = "bidirectionalrelation";

    protected Cloud cloud;
    protected NodeManager relDefManager;
    protected NodeManager typeRelManager;
    protected NodeManager insRelManager; 
    protected NodeManager newsManager; 
    protected NodeManager urlsManager; 

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
    

    /**
     * Create bidirection relation type, and check if relationmanager in both directions can be found.
     */
    public void testBidirectionRelation() {               
        // create a new relation-definition
        Node reldef = relDefManager.createNode();
        reldef.setValue("sname", BIDIRROLE);
        reldef.setValue("dname", BIDIRROLE);
        reldef.setValue("sguiname", BIDIRROLE);
        reldef.setValue("dguiname", BIDIRROLE);
        reldef.setIntValue("dir", 2);
        reldef.setNodeValue("builder", insRelManager);
        reldef.commit();

        Node typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", newsManager);
        typerel.setNodeValue("dnumber", urlsManager);
        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();

        // now this relation must exist.
        try {
            RelationManagerList rm1 = cloud.getRelationManagers(newsManager, urlsManager, BIDIRROLE);
            assertTrue(rm1.size() > 0);
        } catch (NotFoundException e) {
            fail(e.toString());
        }
        try {
            RelationManagerList rm2 = cloud.getRelationManagers(urlsManager, newsManager, BIDIRROLE);
            assertTrue(rm2.size() > 0);
        } catch (NotFoundException e) {
            fail(e.toString());
        }

                    
    }
    /*
     * Create unidirection relation type, and check if relationmanager in only one direction can be found.
     */

    public void testUnidirectionalRelation() {               
        // create a new relation-definition
        Node reldef = relDefManager.createNode();
        reldef.setValue("sname", UNIDIRROLE);
        reldef.setValue("dname", UNIDIRROLE);
        reldef.setValue("sguiname", UNIDIRROLE);
        reldef.setValue("dguiname", UNIDIRROLE);

        reldef.setIntValue("dir", 1);
        reldef.setNodeValue("builder", insRelManager);

        reldef.commit();

        Node typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", newsManager);
        typerel.setNodeValue("dnumber", urlsManager);

        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();

        // now this relation must exist.
        try {
            RelationManagerList rm1 = cloud.getRelationManagers(newsManager, urlsManager, UNIDIRROLE);
            assertTrue(rm1.size() > 0);
        } catch (NotFoundException e) {
            fail(e.toString());
        }
        try {
            RelationManagerList rm2 = cloud.getRelationManagers(urlsManager, newsManager, UNIDIRROLE);
            assertTrue("Found the relations also the other way around, but it is unidirectional", rm2.size() == 0);
        } catch (NotFoundException e) {
            fail(e.toString());
        }

                    
    }


    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        CloudContext cloudContext= ContextProvider.getCloudContext("rmi://127.0.0.1:1111/remotecontext");
        HashMap user = new HashMap();
        user.put("username", "admin");
        user.put("password", "admin5k");
        cloud = cloudContext.getCloud("mmbase","name/password",user);
        relDefManager =  cloud.getNodeManager("reldef");
        typeRelManager = cloud.getNodeManager("typerel");
        insRelManager  = cloud.getNodeManager("insrel");
        newsManager  = cloud.getNodeManager("news");
        urlsManager  = cloud.getNodeManager("urls");
    }
    
}
    
