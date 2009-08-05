/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.statistics;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.math.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class Functions {
    private static final Logger log = Logging.getLoggerInstance(Functions.class);


    public static double sum(Collection<Node> nodeList, String field) {
        double sum = 0;
        for (Node node : nodeList) {
            sum += node.getDoubleValue(field);
        }
        return sum;
    }

    public static Measurement measure() {
        return new Measurement();
    }

    public static Measurement enter(Measurement mes, double value) {
        return mes.enter(value);
    }

    /**
     * Returns the sum of all the field-values of a certain field of all nodes of given node list.
     */
    public static Measurement average(Collection<Node> nodeList, String field) {
        Measurement m = new Measurement();
        for (Node node : nodeList) {
            m.enter(node.getDoubleValue(field));
        }
        return m;
    }
    /**
     * Returns the sum of all the field-values of a certain field of all nodes of given node list.
     */
    public static Measurement times(Measurement m, double dividor) {
        return m.times(dividor);
    }
}
