/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.HashMap;

/**
 *
 * @author Michiel Meeuwissen
 */

public abstract class AbstractTransformer implements Transformer {
 
    protected int to;

    public void configure(int t) {
        to = t;
    }
    public abstract HashMap transformers();

}
