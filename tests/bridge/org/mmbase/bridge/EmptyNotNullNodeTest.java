/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an empty node with 'notnull' fields.
 *
 * @author Michiel Meeuwissen
 */
public class EmptyNotNullNodeTest extends EmptyNodeTest {

    public EmptyNotNullNodeTest(String name) {
        super(name);
    }

    public void testGetValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Object value = node.getValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field did return null, but the field is marked 'notnull'", value != null);
        }
    }


    public void setUp() {
        // Create a empty test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager("xx").createNode();
        // not-null node-field _must_ be filled. Why? It is not the case of other fields.
        // just to aovid the test-case to cause errors, but I think this must be evaluated
        node.setValue("nodefield", cloud.getNodeManager("bb"));
        node.setValue("xmlfield", null);
        node.commit();
    }

}
