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
        controller.setTitle("foo bar");
    }


    protected NodeQuery getQuery(NodeManager items, String start, String end) {
        NodeQuery query = items.createQuery();
        Queries.addConstraint(query,
                              Queries.createConstraint(query, "start", Queries.getOperator("<="), DynamicDate.eval(start)));
        Queries.addConstraint(query,
                              Queries.createConstraint(query, "stop", Queries.getOperator(">="), DynamicDate.eval(end)));
        return query;
    }

    protected void setStartStop(String start, String end) {
        controller.setQuery(getQuery(items, start, end));
        controller.setStart(DynamicDate.eval(start));
        controller.setStop(DynamicDate.eval(end));
    }
    protected void setStartStop(String start, String end, String startForm, String stopForm) {
        setStartStop(start, end);
        controller.setStartForm(DynamicDate.eval(startForm));
        controller.setStopForm(DynamicDate.eval(stopForm));
    }

    protected List<Node> getPeriods(String[]... p) {
        List<Node> periods = new ArrayList<Node>();
        for (String[] startAndStop : p) {
            Node periodNode = items.createNode();
            periodNode.setDateValue("start", DynamicDate.eval(startAndStop[0]));
            periodNode.setDateValue("stop", DynamicDate.eval(startAndStop[1]));
            periodNode.commit();
            periods.add(periodNode);
        }
        return periods;
    }


    //@Test
    public void addToEmpty() throws Exception {
        assertNotNull(items);
        setStartStop("today", "today + 1 day");
        controller.setValue(true);
        List<Node> periods = getPeriods();

        assertEquals(1, controller.fix(periods));
        assertEquals(1, periods.size());

        // another time, not result any changes
        assertEquals(0, controller.fix(periods));
        assertEquals(1, periods.size());
    }

    //@Test
    public void addToPeriod() throws Exception {
        // Add one day more
        setStartStop("today + 1 day", "today + 2 day");
        controller.setValue(true);
        List<Node> periods = getPeriods(new String[] {"today", "today + 1 day"});
        assertEquals(1, controller.fix(periods));
        assertEquals(1, periods.size());
        assertEquals(DynamicDate.eval("today"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 2 day"), periods.get(0).getDateValue("stop"));
    }

    //@Test
    public void addWithGap() throws Exception {
        // Yet another day, but leave a gap
        setStartStop("today + 3 day", "today + 4 day");
        controller.setValue(true);
        List<Node> periods = getPeriods(new String[] {"today", "today + 2 day"});

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


    //@Test
    public void removeAtBeginning() throws Exception {
        // remove first day again
        setStartStop("today", "today + 1 day", "toyear", "toyear + 1 year");
        controller.setValue(false);
        List<Node> periods = getPeriods(new String[] {"today", "today + 2 day"},
                                        new String[] {"today + 3 day", "today + 4 day"}
                                        );


        assertEquals(1, controller.fix(periods));
        assertEquals(2, periods.size());
        assertEquals(DynamicDate.eval("today + 1 day"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 2 day"), periods.get(0).getDateValue("stop"));
        assertEquals(DynamicDate.eval("today + 3 day"), periods.get(1).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 4 day"), periods.get(1).getDateValue("stop"));
        assertEquals(0, controller.fix(periods));
        assertEquals(2, periods.size());
    }

    //@Test
    public void insertAtGap() throws Exception {
        setStartStop("today + 2 day", "today + 3 day");
        controller.setValue(true);
        List<Node> periods = getPeriods(new String[] {"today + 1 day", "today + 2 day"},
                                        new String[] {"today + 3 day", "today + 4 day"}
                                        );

        assertEquals(2, controller.fix(periods));
        assertEquals(1, periods.size());

        assertEquals(DynamicDate.eval("today + 1 day"), periods.get(0).getDateValue("start"));
        assertEquals(DynamicDate.eval("today + 4 day"), periods.get(0).getDateValue("stop"));

        assertEquals(0, controller.fix(periods));
        assertEquals(1, periods.size());
    }

    //@Test
    public void remove() throws Exception {
        List<Node> periods = getPeriods(new String[] {"today + 1 day", "today + 4 day"});
        setStartStop("today + 1 day", "today + 4 day");
        controller.setValue(false);
        assertEquals(1, controller.fix(periods));
        assertEquals(0, periods.size());
        assertEquals(0, controller.fix(periods));
        assertEquals(0, periods.size());
    }
    @Test
    public void makeGap() throws Exception {
        List<Node> periods = getPeriods(new String[] {"today + 1 day", "today + 4 day"});
        setStartStop("today + 2 day", "today + 3 day");
        controller.setValue(false);
        assertEquals(2, controller.fix(periods));
        assertEquals(2, periods.size());
        assertEquals(0, controller.fix(periods));
        assertEquals(2, periods.size());

    }
}
