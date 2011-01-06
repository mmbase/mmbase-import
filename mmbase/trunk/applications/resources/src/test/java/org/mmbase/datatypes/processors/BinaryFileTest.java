/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import java.util.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;


import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 */

public class BinaryFileTest {


    @BeforeClass
    public static void setup() {
        DataTypes.initialize();
        Map<String, DataType> map = new HashMap<String, DataType>();
        map.put("number", Constants.DATATYPE_INTEGER);
        map.put("file", DataTypes.getDataType("file"));
        MockCloudContext.getInstance().addNodeManager("filetest", map);
    }

    @Test
    public void set() {
        NodeManager nm = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("filetest");
        Node n = nm.createNode();
        assertTrue(n.getNumber() < 0);

    }



}
