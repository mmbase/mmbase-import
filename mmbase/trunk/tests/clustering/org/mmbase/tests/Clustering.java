/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.tests;
import junit.framework.TestCase;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.Casting;
import org.w3c.dom.Document;
/**

 * @author Michiel Meeuwissen
 */
public class Clustering extends BridgeTest {

    protected Cloud cloud1;
    protected Cloud cloud2;
    protected NodeList aa2list;
    protected NodeList bb2related;
    protected Node     nodea1;
    public void setUp() {
        cloud1 =   getRemoteCloud("rmi://127.0.0.1:1221/remotecontext");
        cloud2 =   getRemoteCloud("rmi://127.0.0.1:1222/remotecontext");

        NodeManager aa2 = cloud2.getNodeManager("aa");
        NodeManager aa1 = cloud1.getNodeManager("aa");
        NodeManager bb2 = cloud2.getNodeManager("bb");
        aa2list = aa2.getList(null, null, null); // cache list result
        nodea1 = aa1.createNode();
        nodea1.commit();
        NodeQuery nq = Queries.createRelatedNodesQuery(cloud2.getNode(nodea1.getNumber()), bb2, "related", "both");
        bb2related = bb2.getList(nq);
    }

    public void fieldEquals(Node n1, Node n2) {
        FieldIterator fi = n1.getNodeManager().getFields(NodeManager.ORDER_CREATE).fieldIterator();
        while (fi.hasNext()) {
            Field f = fi.nextField();
            if (f.getName().equals("number")) {
                // sigh, 'number' is a NODE field...
                assertTrue(n1.getNumber() == n2.getNumber());
            } else {
                Object value1 = n1.getValue(f.getName());
                Object value2 = n2.getValue(f.getName());
                assertTrue("" + value1 + " != " + value2 + " (value of " + n1.getNumber() + "/" + f.getName() + ")", value1 == null ? value2 == null : value1.equals(value2));
            }
        }
    }

    public void testCreateNode() {
        Node nodea2 = cloud2.getNode(nodea1.getNumber());
        fieldEquals(nodea1, nodea2);
    }

    public void testList() {
        NodeManager aa2 = cloud2.getNodeManager("aa");
        NodeList aa2list2 = aa2.getList(null, null, null);
        assertTrue("Check wether node-list got invalidated failed " +  aa2list2.size()  + " != " + aa2list.size() + " + 1", aa2list2.size() == aa2list.size() + 1);
    }

    private Node getRole(Cloud cl, String name) {
        NodeManager reldef = cl.getNodeManager("reldef");
        NodeQuery nq = reldef.createQuery();
        Constraint c = Queries.createConstraint(nq, "sname", Queries.getOperator("="), name);
        nq.setConstraint(c);
        return reldef.getList(nq).getNode(0);

    }

    public void testCreateTypeRel() {
        NodeManager typerel = cloud1.getNodeManager("typerel");

        Node newTypeRel = typerel.createNode();
        newTypeRel.setValue("rnumber", getRole(cloud1, "posrel"));
        newTypeRel.setValue("snumber", cloud1.getNodeManager("bb"));
        newTypeRel.setValue("dnumber", cloud1.getNodeManager("aa"));
        newTypeRel.commit();

        NodeManager bb2 = cloud2.getNodeManager("bb");
        NodeQuery nq = Queries.createRelatedNodesQuery(cloud2.getNode(nodea1.getNumber()), bb2, null, "both");
        NodeList related2 = bb2.getList(nq);
        assertTrue(related2.size() == 0);
        assertTrue(bb2related.size() == 0);
        
        Node bb1 = cloud1.getNodeManager("bb").createNode();
        bb1.commit();

        Relation r1 = bb1.createRelation(nodea1, (RelationManager) newTypeRel);
        r1.commit();

        related2 = bb2.getList(nq);
        assertTrue("Just created a relation, but related list size is not 1, but " + related2.size(), related2.size() == 1);

        
    }

    public void testInstallBuilder() {

        // create a builder only in of the clouds (1).
        NodeManager typedef = cloud1.getNodeManager("typedef");
        Node zz = typedef.createNode();
        zz.setStringValue("name", "zz");
        Document builderXML = Casting.toXML("<?xml version='1.0' encoding='UTF-8'?>\n" + 
                                            "<!DOCTYPE builder PUBLIC \"-//MMBase//builder config 1.1//EN\" \"http://www.mmbase.org/dtd/builder_1_1.dtd\">\n" + 
                                            "<builder extends='object' maintainer='mmbase.org' name='zz' version='0'></builder>");
        System.out.println("Using builder XML " + org.mmbase.util.xml.XMLWriter.write(builderXML, true));
        zz.setXMLValue("config", builderXML);
        zz.commit();

        NodeManager zz1 = cloud1.getNodeManager("zz");
        assertTrue(cloud1.hasNodeManager("zz"));
        assertTrue(zz1 != null);
        if (cloud2.hasNodeManager("zz")) {
            throw new RuntimeException("Odd, the 'zz' node-manager should only appear in cloud 1 now!, appears in cloud 2 too. Something wrong with test-case, of this issue was solved, and these test-cases need review then.");
        }

    }

    public void testCreateNodeUnknownType() {
        // creating a node of this new type, and look what happens in the cloud which does not know
        // this type.
        Node zNode = cloud1.getNodeManager("zz").createNode();
        zNode.commit();
        Node zNode2 = cloud2.getNode(zNode.getNumber());
        assertTrue("A node of unknown type falls back to 'object'", zNode2.getNodeManager().getName().equals("object"));

    }

    public void testCreateTypeRelUnknownType() {
        NodeManager zz = cloud1.getNodeManager("zz");
        // now create a typerel from aa to zz (which in cloud2 will look quite odd).
        NodeManager typerel = cloud1.getNodeManager("typerel");
        Node newTypeRel = typerel.createNode();
        newTypeRel.setValue("rnumber", getRole(cloud1, "related"));
        newTypeRel.setValue("snumber", cloud1.getNodeManager("aa"));
        newTypeRel.setValue("dnumber", zz);
        newTypeRel.commit();
        
        Node zNode1 = zz.createNode(); zNode1.commit();
        Node zNode2 = zz.createNode(); zNode2.commit();
        Relation r1 = nodea1.createRelation(zNode1, (RelationManager) newTypeRel); r1.commit();
        Relation r2 = nodea1.createRelation(zNode2, (RelationManager) newTypeRel); r2.commit();

        // now ask the number of related nodes from nodea1;
        Node nodea2 = cloud2.getNode(nodea1.getNumber());

        assertTrue(nodea1.getRelatedNodes().size() == nodea2.getRelatedNodes().size());
        
                            

    }
}

