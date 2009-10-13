/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.tests.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class QueriesTest extends BridgeTest {


    public QueriesTest(String name) {
        super(name);
    }


    public void testConstants() {

        assertTrue(Queries.getRelationStepDirection("destination") == RelationStep.DIRECTIONS_DESTINATION);
        assertTrue(Queries.getRelationStepDirection("SOURCE") == RelationStep.DIRECTIONS_SOURCE);
        try {
            Queries.getRelationStepDirection("bla");
            fail("Should have thrown exception");
        } catch (BridgeException be) {};

    }


}
