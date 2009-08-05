/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DataTypesTestTransaction extends DataTypesTest {


    public DataTypesTestTransaction(String name) {
        super(name);
    }
    protected Cloud getCloud() {
        return getTransaction();
    }

    protected byte[] getBinary() {
        return new byte[] {1, 2, 3, 4, 5};
    }
}

