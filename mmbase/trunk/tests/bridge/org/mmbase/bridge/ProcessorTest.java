/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import junit.framework.*;

import org.mmbase.util.DynamicDate;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ProcessorTest.java,v 1.2 2008-11-25 09:17:25 michiel Exp $
 * @since MMBase-1.9.1
  */
public class ProcessorTest extends BridgeTest {
    private static final Logger log = Logging.getLoggerInstance(TransactionTest.class);

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

    protected void testCommitProcessorIsChanged2(Cloud c, int nn ) {
        Node n = c.getNode(nn);
        n.setStringValue("string", "blie");
        n.commit();
    }
    protected void testCommitProcessorIsChanged3(Cloud c, int nn ) {
        Node n = c.getNode(nn);
        try {
            n.commit();
            throw new AssertionFailedError("Should have thrown exception");
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


    protected int testAge(Cloud c) {
        NodeManager nm = c.getNodeManager("datatypes");
        Node n = nm.createNode();
        n.setDateValue("birthdate", DynamicDate.eval("2008-01-01"));
        n.commit();
        assertEquals(DynamicDate.eval("2008-01-01"), n.getDateValue("birthdate"));
        n.setIntValue("age", 10);
        n.commit();
        assertEquals(10, n.getIntValue("age"));
        return n.getNumber();
    }

    public void testAge() {
        //org.mmbase.cache.CacheManager.getInstance().disable(".*");
        testAge(getCloud());
        org.mmbase.cache.CacheManager.getInstance().readConfiguration();
    }
    public void testAgeTransaction() {
        org.mmbase.cache.CacheManager.getInstance().disable(".*");
        Transaction t = getCloud().getTransaction("bla");
        int n = testAge(t);
        t.commit();
        assertEquals(10, getCloud().getNode(n).getIntValue("age"));
        org.mmbase.cache.CacheManager.getInstance().readConfiguration();
    }

}
