package org.mmbase.core.event;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class NodeEventTest  {

    @Test
    public void basic() throws Exception {
        Map<String, Object> newValues = new HashMap<String, Object>();
        newValues.put("a", "AA");
        newValues.put("b", new byte[] { 0, 1, 2} );
        System.out.println("required: " + Arrays.asList(NodeEvent.getRequiredValueTypes()));
        System.out.println("unacceptable: " + Arrays.asList(NodeEvent.getUnacceptableValueTypes()));
        NodeEvent ev = new NodeEvent("localhost", "object", 123, null, newValues, Event.TYPE_CHANGE);
        newValues.put("a", "BB");
        assertEquals("AA", ev.getNewValue("a"));
        assertEquals(null, ev.getNewValue("b"));
    }
}
