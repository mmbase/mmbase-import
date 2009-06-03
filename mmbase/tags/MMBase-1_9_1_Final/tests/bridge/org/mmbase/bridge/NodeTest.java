/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Iterator;
import java.util.regex.Pattern;
import org.mmbase.tests.*;
import org.mmbase.security.AuthenticationData;

/**
 * Basic test class to test <code>Node</code> from the bridge package.
 *
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 */
public abstract class NodeTest extends BridgeTest {
    protected Node node;
    protected final static String[] fieldTypes = {"float", "int", "long", "string", "xml", "node", "datetime", "boolean", "decimal", "double", "binary"}; //, "list"};
    //protected static String[] fieldTypes = {"datetime"};

    public NodeTest(String name) {
        super(name);
    }

    protected String getNodeManager() {
        return "aa";
    }

    abstract public void testGetValue();

    public void testGetValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetDecimalValue();
        testGetListValue();
    }

    abstract public void testGetBinaryValue();

    public void testGetBinaryValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetBinaryValue();
        testGetValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetDecimalValue();
        testGetListValue();
    }

    abstract public void testGetDoubleValue();

    public void testGetDoubleValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetDoubleValue();
        testGetValue();
        testGetBinaryValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetDecimalValue();
        testGetListValue();
    }

    abstract public void testGetFloatValue();

    public void testGetFloatValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetFloatValue();
        testGetValue();
        testGetBinaryValue();
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
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetListValue();
    }

    abstract public void testGetLongValue();

    public void testGetLongValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetLongValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetDecimalValue();
        testGetListValue();
    }

    abstract public void testGetStringValue();

    public void testGetStringValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetStringValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetDecimalValue();
        testGetListValue();
    }

    abstract public void testGetXMLValue();

    public void testGetXMLValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetXMLValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetListValue();
    }

    abstract public void testGetNodeValue();

    public void testGetNodeValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetNodeValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetBooleanValue();
        testGetDateTimeValue();
        testGetListValue();
    }

    abstract public void testGetBooleanValue();

    public void testGetBooleanValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetBooleanValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetDateTimeValue();
        testGetDecimalValue();
        testGetListValue();
    }

    abstract public void testGetDateTimeValue();

    public void testGetDateTimeValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetDateTimeValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetListValue();
    }

    abstract public void testGetDecimalValue();

    abstract public void testGetListValue();

    public void testGetListValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetListValue();
        testGetValue();
        testGetBinaryValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
        testGetXMLValue();
        testGetNodeValue();
        testGetBooleanValue();
        testGetDateTimeValue();
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
        for (String alias : node.getAliases()) {
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

        if (Boolean.TRUE.equals(getCloudContext().getAuthentication().getAttribute(AuthenticationData.STORES_CONTEXT_IN_OWNER))) {
            String context = node.getContext();
            String otherContext = getOtherContext(node);

            if (otherContext.equals(context)) {
                otherContext = context + "other";
                System.err.println("Could not find other context than " + context + ", setting to '" + otherContext + "'");
            }

            // set context to something different:
            node.setContext(otherContext);
            // now, the new context must be equal to otherContext
            assertEquals("Context did not change '" + otherContext + "' != '" + node.getContext() + "'", otherContext, node.getContext());
            assertEquals(node.getContext(), node.getValue("owner"));
            node.commit();

            assertEquals("Context did not change '" + otherContext + "' != '" + node.getContext() + "'", otherContext, node.getContext());
            assertEquals(node.getContext(), node.getValue("owner"));
        } else {
            System.err.println("Warning: could not execute 'set owner' test, because security authorization implemention does not store context in owner field");
        }
    }

    public void testSetOwner() {
        if (Boolean.TRUE.equals(getCloudContext().getAuthentication().getAttribute(AuthenticationData.STORES_CONTEXT_IN_OWNER))) {
            String context = node.getContext();
            String otherContext = getOtherContext(node);

            if (otherContext.equals(context)) {
                otherContext = context + "other";
                System.err.println("Could not find other context than " + context + ", setting to '" + otherContext + "'");
            }

            // set context to something different:
            node.setValue("owner", otherContext);
            // now, the new context must be equal to otherContext
            assertEquals("Context did not change '" + otherContext + "' != '" + node.getContext() + "'", otherContext, node.getContext());
            assertEquals(node.getContext(), node.getValue("owner"));

            node.commit();

            assertEquals("Context did not change '" + otherContext + "' != '" + node.getContext() + "'", otherContext, node.getContext());
            assertEquals(node.getContext(), node.getValue("owner"));
        } else {
            System.err.println("Warning: could not execute 'set owner' test, because security authorization implemention does not store context in owner field");
        }


    }
    public void testFieldGUI() {
        try {
            node.getStringValue("gui()");
            node.getValue("gui()");
        } catch (Throwable  e) {
            fail("Should not raise exception but gave: " + e.getMessage() + org.mmbase.util.logging.Logging.stackTrace(e, 20));
        }
    }

    private static final Pattern NO_FIELD = Pattern.compile("(?i).*field.*nonexistingfield.*");
    public void testNonExistingField() {
        try {
            node.getStringValue("nonexistingfield");
            fail("Getting non existing field should throw a (clear) exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage() + " does not match " + NO_FIELD, NO_FIELD.matcher(e.getMessage()).matches());
        }

        try {
            node.getFieldValue("nonexistingfield");
            fail("Getting non existing field should throw a (clear) exception");
        } catch (NotFoundException e) {
            assertTrue(e.getMessage() + " does not match " + NO_FIELD, NO_FIELD.matcher(e.getMessage()).matches());
        }
    }


}
