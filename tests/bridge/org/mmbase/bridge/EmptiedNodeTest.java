/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an explicitly emptied node.
 *
 * @author Michiel Meeuwissen
 */
public class EmptiedNodeTest extends EmptyNodeTest {

    public EmptiedNodeTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a empty test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager("aa").createNode();
        node.setValue("bytefield", null);
        node.setValue("doublefield", null);
        node.setValue("floatfield", null);
        node.setValue("intfield", null);
        node.setValue("longfield", null);
        node.setValue("stringfield", null);
        node.commit();
    }

}
