/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Iterator;
import org.mmbase.tests.*;

/**
 * Basic test class to test <code>Node</code> from the bridge package.
 *
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 */
public abstract class NodeTest extends BridgeTest {
    Node node;
    String[] fieldTypes = {"byte", "double", "float", "int", "long", "string"};

    public NodeTest(String name) {
        super(name);
    }

    abstract public void testGetValue();
    
    public void testGetValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    abstract public void testGetByteValue();

    public void testGetByteValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetByteValue();
        testGetValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    abstract public void testGetDoubleValue();

    public void testGetDoubleValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetDoubleValue();
        testGetValue();
        testGetByteValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    abstract public void testGetFloatValue();

    public void testGetFloatValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetFloatValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    abstract public void testGetIntValue();

    public void testGetIntValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetIntValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetLongValue();
        testGetStringValue();
    }

    abstract public void testGetLongValue();

    public void testGetLongValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetLongValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetStringValue();
    }

    abstract public void testGetStringValue();

    public void testGetStringValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetStringValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
    }

    public void testSetSNumber() {
        try {
            node.setIntValue("snumber", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetDNumber() {
        try {
            node.setIntValue("dnumber", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetRNumber() {
        try {
            node.setIntValue("rnumber", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetOwner() {
        try {
            node.setStringValue("owner", "admin");
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
        try {
            node.setValue("owner", "admin");
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetOType() {
        try {
            node.setIntValue("otype", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testCreateAlias() {
        try {
            node.createAlias("node_alias");       
            node.commit();
            // look it up again
            boolean found = false;
            Iterator i = node.getAliases().iterator();
            while (i.hasNext()) {
                String alias = (String)i.next();
                if ("node_alias".equals(alias)) found = true;
            }        
            assertTrue(found);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testSetContext() {
        try {
            String context = node.getContext();
            String otherContext = context;
            StringIterator possibleContexts = node.getPossibleContexts().stringIterator();
            while (possibleContexts.hasNext()) {
                String listContext = possibleContexts.nextString();              
                if (! context.equals(listContext)){
                    otherContext = listContext;
                    break;
                }
            }
            if (otherContext.equals(context)) {
                System.err.println("TESTWARNING testSetContext: Could not find other context than " + context);
            }

            // set context to something different:
            node.setContext(otherContext);

            // now, the new context must be equal to otherContext
            assertTrue(otherContext.equals(node.getContext()));

        } catch (Exception e){
            fail(e.toString());
        }
        
    }

}
