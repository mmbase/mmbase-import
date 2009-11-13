/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import org.mmbase.tests.*;
import org.mmbase.core.event.*;

/**
 * Testing events and bridge.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class EventsTest extends BridgeTest {

    public EventsTest(String name) {
        super(name);
    }

    public void testEvents() throws InterruptedException {

        // register a event listener here.
        AllEventListener listener = new AllEventListener() {
                List<Event> events = new ArrayList<Event>();
                public void notify(Event e) {
                    events.add(e);
                }
            };

        Thread thread1 = new Thread() {
                public void run() {
                    // cause events
                    Event event = new Event("thismachine") { };
                    // how to send it?

                    Cloud cloud = getCloud();
                    Node node = cloud.getNodeManager("aa").createNode();
                    node.commit();
                    // should have caused a create event.


                    // cause more events.
                }
            };

        thread1.start();
        thread1.join();

        // assert if expected events are in listener

    }

}
