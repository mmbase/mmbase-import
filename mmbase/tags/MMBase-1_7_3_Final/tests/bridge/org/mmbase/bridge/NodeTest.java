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
    protected Node node;
    protected String[] fieldTypes = {"byte", "double", "float", "int", "long", "string", "xml", "node"};

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
        testGetXMLValue();
        testGetNodeValue();
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
        testGetXMLValue();
        testGetNodeValue();
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
        testGetXMLValue();
        testGetNodeValue();
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
        testGetXMLValue();
        testGetNodeValue();
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
        testGetXMLValue();
        testGetNodeValue();
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
        testGetXMLValue();
        testGetNodeValue();
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
        testGetXMLValue();
        testGetNodeValue();
    }

    abstract public void testGetXMLValue();

    public void testGetXMLValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetXMLValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetNodeValue();
    }

    abstract public void testGetNodeValue();

    public void testGetNodeValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetNodeValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
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
    }

    protected String getOtherContext(Node n) {        
        String context = n.getContext();
        StringIterator possibleContexts = n.getPossibleContexts().stringIterator();
        while (possibleContexts.hasNext()) {
            String listContext = possibleContexts.nextString();              
            if (! context.equals(listContext)){
                return listContext;
            }
        }
        return context;
    }

    
    public void testSetContext() {
        String context = node.getContext();
        String otherContext = getOtherContext(node);
     
        if (otherContext.equals(context)) {
            otherContext = context + "other";
            System.err.println(this.getClass().getName() + " TESTWARNING testSetContext: Could not find other context than " + context + ", setting to '" + otherContext + "'");
        }
        
        // set context to something different:
        node.setContext(otherContext);
        
        // now, the new context must be equal to otherContext
        assertTrue("KNOWN - bug #6168:", otherContext.equals(node.getContext()));
    }

}
