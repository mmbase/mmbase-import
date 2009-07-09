/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.tests.*;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 */
public class VirtualNodeTest extends BridgeTest {


    public VirtualNodeTest(String name) {
        super(name);
    }


    public void testBasic() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", "A");

        VirtualNode node = new VirtualNode(map, getCloud());
        assertEquals("A", node.getStringValue("a"));

    }


    public void testNodeValue() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", "A");


        Map<String, Object> sub = new HashMap<String, Object>();
        sub.put("b", "B");

        VirtualNode subNode = new VirtualNode(sub, getCloud());
        map.put("subnode", subNode);


        VirtualNode node = new VirtualNode(map, getCloud());
        assertEquals("A", node.getStringValue("a"));

        assertNotNull("" + node, node.getNodeValue("subnode"));
        assertEquals("B", node.getNodeValue("subnode").getStringValue("b"));

    }


}
