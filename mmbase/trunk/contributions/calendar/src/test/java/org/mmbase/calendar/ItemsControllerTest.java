/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.calendar;

import org.mmbase.bridge.mock.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @version $Id: ContextTagTest.java 39651 2009-11-11 18:17:50Z michiel $
 */

public  class ItemsControllerTest {

    private static ItemsController controller = new ItemsController();
    private static NodeManager items;
    private static List<Node> periods = new ArrayList<Node>();


    public CloudContext getCloudContext() {
        return MockCloudContext.getInstance();
    }
    @BeforeClass()
    public static void setUp() throws Exception {
        DataTypes.initialize();
        MockCloudContext.getInstance().clear();
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("calendar"));
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        items = cloud.getNodeManager("calendar_items");
    }


    protected NodeQuery getQuery(NodeManager items, String start, String end) {
        NodeQuery query = items.createQuery();
        Queries.addConstraint(query,
                              Queries.createConstraint(query, "start", Queries.getOperator("<"), DynamicDate.eval(start)));
        Queries.addConstraint(query,
                              Queries.createConstraint(query, "stop", Queries.getOperator(">"), DynamicDate.eval(end)));
        return query;
    }


    @Test
    public void addToEmpty() throws Exception {
        assertNotNull(items);
        controller.setQuery(getQuery(items, "today", "today + 1 day"));
        controller.setTitle("foo bar");
        controller.setValue(true);

        assertEquals(1, controller.fix(periods));
        assertEquals(1, periods.size());

        // another time, not result any changes
        assertEquals(0, controller.fix(periods));
        assertEquals(1, periods.size());
    }

    @Test
    public void addToPeriod() throws Exception {
        // Add one day more
        controller.setQuery(getQuery(items, "today + 1 day", "today + 2 day"));
        assertEquals(1, controller.fix(periods));
        assertEquals(1, periods.size());
        assertEquals(DynamicDate.eval("today"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 2 day"), periods.get(0).getDateValue("stop"));
    }

    @Test
    public void addWithGap() throws Exception {
        // Yet another day, but leave a gap
        controller.setQuery(getQuery(items, "today + 3 day", "today + 4 day"));
        assertEquals(1, controller.fix(periods));
        assertEquals(2, periods.size());
        assertEquals(DynamicDate.eval("today"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 2 day"), periods.get(0).getDateValue("stop"));
        assertEquals(DynamicDate.eval("today + 3 day"), periods.get(1).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 4 day"), periods.get(1).getDateValue("stop"));
        // second time, nothing should happen
        assertEquals(0, controller.fix(periods));
        assertEquals(2, periods.size());
    }


    @Test
    public void removeAtBeginning() throws Exception {
        // remove first day again
        controller.setQuery(getQuery(items, "today", "today + 1 day"));
        controller.setValue(false);
        assertEquals(1, controller.fix(periods));
        assertEquals(2, periods.size());
        assertEquals(DynamicDate.eval("today + 1 day"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 2 day"), periods.get(0).getDateValue("stop"));
        assertEquals(DynamicDate.eval("today + 3 day"), periods.get(1).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 4 day"), periods.get(1).getDateValue("stop"));
        assertEquals(0, controller.fix(periods));
        assertEquals(2, periods.size());
    }

    @Test
    public void insertAtGap() throws Exception {
        controller.setQuery(getQuery(items, "today + 2 day", "today + 3 day"));
        controller.setValue(true);
        assertEquals(2, controller.fix(periods));
        assertEquals(1, periods.size());

        assertEquals(DynamicDate.eval("today + 1 day"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 4 day"), periods.get(0).getDateValue("stop"));

        assertEquals(0, controller.fix(periods));
        assertEquals(1, periods.size());
    }
}
