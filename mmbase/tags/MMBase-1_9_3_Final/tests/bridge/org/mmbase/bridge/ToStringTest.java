/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.util.*;
import org.mmbase.tests.*;
import junit.framework.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 */
public class ToStringTest extends BridgeTest {


    public ToStringTest(String name) {
        super(name);
    }

    //
    public void testClusterNode() {
        Cloud cloud = getCloud();
        NodeList nl = cloud.getList("", "object", "",
                                    "", "", "",
                                    "", false);
        for (Node n : nl) {
            assertTrue(n.getNumber() < 0); // silly way to test for viruality
            assertFalse("".equals(n.toString())); //MMB-333
        }
    }


}
