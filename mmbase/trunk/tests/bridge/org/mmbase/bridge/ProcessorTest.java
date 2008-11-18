/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import junit.framework.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ProcessorTest.java,v 1.1 2008-11-18 23:29:32 michiel Exp $
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


}
