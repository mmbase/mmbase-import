/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.test.bridge;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Transaction;

public class FilledNodeTestTransaction extends FilledNodeTest {

    public FilledNodeTestTransaction(String name) {
        super(name);
    }

    protected Cloud getCloud() {
        return  super.getCloud().getTransaction("test_transaction");
    }


    public void tearDown() {
        // simply roll back transaction
        Transaction trans = (Transaction) getCloud();
        trans.cancel();
    }


}
