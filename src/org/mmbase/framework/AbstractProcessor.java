/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.util.functions.*;

/**
 * Abstract view implementation which implements getType and the specific parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractProcessor.java,v 1.3 2006-11-07 21:44:35 michiel Exp $
 * @since MMBase-1.9
 */
abstract public class AbstractProcessor implements Processor {

    public static Processor getEmpty(final Block b)  {
        return new Processor() {
            public Block getBlock() { return b; }
            public Parameter[] getParameters() { return Parameter.EMPTY; }
            public void process(Parameters blockParameters, Parameters frameworkParameters) { }
            public String toString() { return "EMPTY Processor"; }
        };
    }
    protected final Block parent;

    public AbstractProcessor(Block p) {
        parent = p;
    }

    public Block getBlock() {
        return parent;
    }

}
