/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import org.mmbase.datatypes.processors.*;
import junit.framework.*;
import org.mmbase.bridge.util.*;

import org.mmbase.util.DynamicDate;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Testing wether the processors of datatypes behave as expected.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
  */
public class ProcessorTest extends BridgeTest {
    private static final Logger log = Logging.getLoggerInstance(TransactionTest.class);

    static long counter = 0;

    public ProcessorTest(String name) {
        super(name);
    }


    protected Node testCommitProcessorIsChanged1(Cloud c) {
        NodeManager nm = c.getNodeManager("mustbechanged");
        Node n = nm.createNode();
        n.setStringValue("string", "bla");
        n.commit();
        return n;
    }

    protected void testCommitProcessorIsChanged2(Cloud c, int nn) {
        Node n = c.getNode(nn);
        n.setStringValue("string", "blie");
        n.commit();
    }
    protected void testCommitProcessorIsChanged3(Cloud c, int nn) {
        Node n = c.getNode(nn);
        try {
            n.commit();
            fail("Should have thrown exception");
        } catch(RuntimeException ru) {
            // ok
        }
    }

    public void testCommitProcessorIsChanged() {
        Cloud c = getCloud();
        int nn  = testCommitProcessorIsChanged1(c).getNumber();
        testCommitProcessorIsChanged2(c, nn);
        testCommitProcessorIsChanged3(c, nn);
    }

    public void testCommitProcessorIsChangedTransaction() {
        Cloud c = getCloud();
        Transaction t = c.getTransaction("aa");
        Node n  = testCommitProcessorIsChanged1(t);
        t.commit();
        int nn = n.getNumber();
        t = c.getTransaction("bb");
        testCommitProcessorIsChanged2(t, nn);
        t.commit();
        t = c.getTransaction("cc");
        testCommitProcessorIsChanged3(t, nn);
        t.commit();
    }


    protected Node testAge(Cloud c) {
        NodeManager nm = c.getNodeManager("datatypes");
        Node n = nm.createNode();
        n.setDateValue("birthdate", DynamicDate.eval("2008-01-01"));
        n.commit();
        n = c.getNode(n.getNumber());
        assertEquals(DynamicDate.eval("2008-01-01"), n.getDateValue("birthdate"));
        n.setIntValue("age", 10);
        assertEquals(10, n.getIntValue("age"));
        n.commit();
        assertEquals(10, n.getIntValue("age"));
        return n;
    }

    public void testAge() {
        try {
            org.mmbase.cache.CacheManager.getInstance().disable(".*");
            testAge(getCloud());
            org.mmbase.cache.CacheManager.getInstance().readConfiguration();
        } catch (NoClassDefFoundError ncdfe) {
            log.service("Probably using RMMCI, cannot disable caches then. " + ncdfe.getMessage());
        }
    }
    public void testAgeTransaction() {
        try {
            org.mmbase.cache.CacheManager.getInstance().disable(".*");
            Transaction t = getCloud().getTransaction("bla");
            Node n = testAge(t);
            t.commit();
            assertEquals(10, getCloud().getNode(n.getNumber()).getIntValue("age"));
            org.mmbase.cache.CacheManager.getInstance().readConfiguration();
        } catch (NoClassDefFoundError ncdfe) {
            log.service("Probably using RMMCI, cannot disable caches then. " + ncdfe.getMessage());
        }

    }

    // Creates an commits a node, and checks if that increased the commit count
    protected void testCommitCount(Cloud c) {
        if (getCloudContext().getUri().equals(ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME)) { // only test on local
            NodeManager nm = c.getNodeManager("datatypes");
            int ccbefore = CountCommitProcessor.count;
            Node n = nm.createNode();
            n.commit();
            assertEquals(ccbefore + 1, CountCommitProcessor.count);
        }
    }

    public void testCommitCount() {
        testCommitCount(getCloud());
    }

    public void testCommitCountTransaction() {
        int ccbefore = CountCommitProcessor.count;
        Transaction t = getCloud().getTransaction("commitcount");
        testCommitCount(t);
        t.commit();
        if (getCloudContext().getUri().equals(ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME)) { // only test on local
            assertEquals(ccbefore + 1, CountCommitProcessor.count); // there is no point in calling a commit processor twice
        }
    }

    static int nn = 0;
    public void testCommitCountTransaction2() {
        Transaction t = getCloud().getTransaction("commitcount2");
        int ccbefore = CountCommitProcessor.count;
        NodeManager nm = t.getNodeManager("datatypes");
        Node n = nm.createNode();
        // not committing node
        t.commit(); // but only the transaction.

        nn = n.getNumber();
        if (getCloudContext().getUri().equals(ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME)) { // only test on local
            // commit processor must have been called.
            assertEquals(ccbefore + 1, CountCommitProcessor.count);
        }
    }


    protected void testCommitCountOnChange(Cloud c, int nn) {
        int ccbefore = CountCommitProcessor.count;
        int changedbefore = CountCommitProcessor.changed;
        Node n = c.getNode(nn);
        n.commit();
        if (getCloudContext().getUri().equals(ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME)) {
            assertEquals(ccbefore + 1, CountCommitProcessor.count);
            assertEquals(changedbefore, CountCommitProcessor.changed);
        }
    }
    protected void testCommitCountOnChange2(Cloud c, int nn) {
        int ccbefore = CountCommitProcessor.count;
        int changedbefore = CountCommitProcessor.changed;
        Node n = c.getNode(nn);
        n.setStringValue("string", "foobar" + (counter++));
        assertTrue("Node is changed, but it reports that it isn't. Values" + new NodeMap(n) + " changed fields " + n.getChanged(), n.isChanged());
        n.commit();
        if (getCloudContext().getUri().equals(ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME)) {
            assertEquals(ccbefore + 1, CountCommitProcessor.count);
            assertEquals(changedbefore + 1,  CountCommitProcessor.changed);
        }
    }

    public void testCommitCountOnChange() {
        testCommitCountOnChange(getCloud(), nn);
        testCommitCountOnChange2(getCloud(), nn);
    }

    public void testCommitCountOnChangeTransaction() {
        testCommitCountOnChange(getCloud().getTransaction("commitcount3"), nn);
        testCommitCountOnChange2(getCloud().getTransaction("commitcount4"), nn);
    }



}
