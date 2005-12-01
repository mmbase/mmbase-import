package org.mmbase.cache;

import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.util.Queries;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.tests.BridgeTest;

public class ConstraintMatcherTest extends BridgeTest {
    static protected Cloud cloud = null;
    
    static protected ConstraintsMatchingStrategy matchingStrategy ;
    
    public ConstraintMatcherTest(String name){
        super(name);
    }

    public  void setUp() throws Exception{
        if (cloud == null) {   
            startMMBase();
            cloud = getCloud();
        }
        matchingStrategy = new ConstraintsMatchingStrategy();
    }
    
    protected static Map createMap(Object[][] objects){
        Map map = new HashMap();
        for (int i = 0; i < objects.length; i++) {
            map.put(objects[i][0], objects[i][1]);
        }
        return map;
    }
        
        
    public void testBasicCompositeConstraintMatcher(){
        
    }
    
    public void testBasicFieldValueConstraintMatcher(){
        //if the field of a new node dous not fall within the constraint of a query: don't flush 
        Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls", "news.title","news.title = 'disco'",null,null,null,false);
        
        NodeEvent event1 = new NodeEvent(null, "news", 10, new HashMap(), createMap(new String[][] { {"title","hallo"} }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event2 = new NodeEvent(null, "news", 10, new HashMap(), createMap(new String[][] { {"title","disco"} }), NodeEvent.EVENT_TYPE_CHANGED);
        
        assertFalse("Changed node falls outside query constraints. no flush", matchingStrategy.evaluate(event1, q1, null).shouldRelease());
        assertTrue("Changed node falls within query constraints. flush", matchingStrategy.evaluate(event2, q1, null).shouldRelease());
    }


}


