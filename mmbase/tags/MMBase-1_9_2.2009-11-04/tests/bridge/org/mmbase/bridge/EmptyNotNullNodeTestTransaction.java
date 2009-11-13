/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an empty node inside a transaction.
 *
 * @author Michiel Meeuwissen
 */
public class EmptyNotNullNodeTestTransaction extends EmptyNotNullNodeTest {

    public EmptyNotNullNodeTestTransaction(String name) {
        super(name);
    }

    protected Cloud getCloud() {
        return getTransaction();
    }


    public void tearDown() {
        // simply roll back transaction
        Transaction trans = (Transaction) getCloud();
        trans.cancel();
    }

}
